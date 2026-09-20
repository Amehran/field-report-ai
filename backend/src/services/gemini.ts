import { GoogleGenAI, Type, Schema } from '@google/genai';
import * as admin from 'firebase-admin';

// Initialize the Google Gen AI SDK
const ai = new GoogleGenAI({});

export interface DraftGenerationParams {
  jobTitle: string;
  customerName: string;
  typedNotes?: string;
  mediaUris: string[]; // List of gs:// URIs for photos and audio
}

export interface GeneratedDraft {
  workCompletedJson?: string;
  findingsJson?: string;
  recommendationsJson?: string;
  initialStatus: string;
  resolutionStepsJson: string;
  currentOperationalState: string;
  estimatedLaborCost?: number;
  estimatedPartsCost?: number;
}

// Define the expected output schema using the standard Schema interface from @google/genai
const draftSchema: Schema = {
  type: Type.OBJECT,
  properties: {
    initialStatus: {
      type: Type.STRING,
      description: "A professional summary of the initial problem observed or the reason for the service call."
    },
    resolutionStepsJson: {
      type: Type.STRING,
      description: "A comprehensive bulleted list (separated by '||') of physical repair, installation, adjustment, or testing actions taken."
    },
    currentOperationalState: {
      type: Type.STRING,
      description: "A summary of the post-repair status and operational state of the equipment or system."
    },
    estimatedLaborCost: {
      type: Type.NUMBER,
      description: "Optional estimated labor cost if specifically mentioned in the audio/notes, otherwise omit."
    },
    estimatedPartsCost: {
      type: Type.NUMBER,
      description: "Optional estimated parts cost if specifically mentioned in the audio/notes, otherwise omit."
    },
    workCompletedJson: {
      type: Type.STRING,
      description: "Legacy field for backward compatibility. Provide the same content as resolutionStepsJson."
    },
    findingsJson: {
      type: Type.STRING,
      description: "Legacy field. A comprehensive bulleted list (separated by '||') of any findings or observations."
    },
    recommendationsJson: {
      type: Type.STRING,
      description: "Legacy field. A comprehensive bulleted list (separated by '||') of recommended next steps."
    }
  },
  required: ["initialStatus", "resolutionStepsJson", "currentOperationalState"]
};

async function getInlineMediaData(uri: string): Promise<{ mimeType: string; data: string } | null> {
  try {
    let mimeType = 'image/jpeg';
    if (uri.endsWith('.png')) mimeType = 'image/png';
    else if (uri.endsWith('.m4a')) mimeType = 'audio/mp4';
    else if (uri.endsWith('.mp3')) mimeType = 'audio/mp3';
    else if (uri.endsWith('.mp4')) mimeType = 'video/mp4';

    if (uri.startsWith('gs://')) {
      if (admin.apps.length === 0) {
        admin.initializeApp();
      }
      const match = uri.match(/^gs:\/\/([^\/]+)\/(.+)$/);
      if (match) {
        const bucketName = match[1];
        const filePath = match[2];
        const file = admin.storage().bucket(bucketName).file(filePath);
        const [buffer] = await file.download();
        return {
          mimeType,
          data: buffer.toString('base64')
        };
      }
    } else if (uri.startsWith('data:')) {
      const parts = uri.split(',');
      const header = parts[0];
      const data = parts[1];
      const mimeMatch = header.match(/data:(.*?);/);
      const extractedMime = mimeMatch ? mimeMatch[1] : mimeType;
      return {
        mimeType: extractedMime,
        data
      };
    }
    return null;
  } catch (err) {
    console.warn(`Could not fetch inline media for ${uri}, falling back to fileData reference:`, err);
    return null;
  }
}

export async function generateReportDraft(params: DraftGenerationParams): Promise<GeneratedDraft> {
  const { jobTitle, customerName, typedNotes, mediaUris } = params;

  // Prepare the prompt
  let promptText = `
    You are an expert, professional field service technician documenting an official job completion report.

    Primary Job Context:
    - Customer / Job Name: ${customerName}
    - Specific Work / Trade Title: ${jobTitle}
    ${typedNotes ? `- Technician Typed Notes: ${typedNotes}` : ''}

    CRITICAL RULES & GUIDELINES:
    1. STRICT JOB RELEVANCE: Base all text strictly on "${jobTitle}", technician notes, voice recording audio, and photos. If the job is a Thermostat Repair, write specifically about thermostat components, wiring, sensors, temperature calibration, HVAC relays, voltage readings, and climate control operation. Never mention unrelated areas like "kitchen door" or generic boilerplate.
    2. THE WORK DESCRIPTION IS THE MOST IMPORTANT CONTEXT: Extract every detail from the technician's notes, voice note audio, and photos to describe the actual physical work performed step-by-step.
    3. ABSOLUTELY NO META-COMMENTARY: Never write phrases like "In the voice note...", "According to the transcript...", "The audio says...", "Inspecting the photos...", or "Notes indicate...". Write directly in the professional active voice of the performing technician detailing the physical service.
    4. SECTION REQUIREMENTS:
       - initialStatus: A professional summary of the initial problem observed or the reason for the service call.
       - resolutionStepsJson: Detailed, technical bulleted list (separated by '||') of physical repair, installation, adjustment, or testing actions taken specifically for ${jobTitle}.
       - currentOperationalState: A summary of the post-repair status and operational state of the equipment or system.
       - estimatedLaborCost (optional): Extract any mentioned labor cost.
       - estimatedPartsCost (optional): Extract any mentioned parts cost.
       - workCompletedJson, findingsJson, recommendationsJson: Fill these legacy fields with the same detailed bullet points (separated by '||') for backward compatibility.
  `;

  // Prepare contents for the API
  const contents: any[] = [
    { text: promptText }
  ];

  for (const uri of mediaUris) {
    let mimeType = 'image/jpeg';
    if (uri.endsWith('.png')) mimeType = 'image/png';
    else if (uri.endsWith('.m4a')) mimeType = 'audio/mp4';
    else if (uri.endsWith('.mp3')) mimeType = 'audio/mp3';
    else if (uri.endsWith('.mp4')) mimeType = 'video/mp4';

    const inlineData = await getInlineMediaData(uri);
    if (inlineData) {
      contents.push({
        inlineData: {
          mimeType: inlineData.mimeType,
          data: inlineData.data
        }
      });
    } else {
      contents.push({
        fileData: {
          fileUri: uri,
          mimeType: mimeType
        }
      });
    }
  }

  try {
    const response = await ai.models.generateContent({
      model: 'gemini-2.5-flash',
      contents: contents,
      config: {
        responseMimeType: 'application/json',
        responseSchema: draftSchema,
        temperature: 0.2
      }
    });

    const responseText = response.text;
    if (!responseText) {
      throw new Error("Gemini returned an empty response.");
    }

    const draft = JSON.parse(responseText) as GeneratedDraft;
    return draft;
  } catch (error) {
    console.error("Error generating draft from Gemini:", error);
    throw new Error("Failed to generate report draft.");
  }
}
