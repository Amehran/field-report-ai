import { GoogleGenAI, Type, Schema } from '@google/genai';

// Initialize the Google Gen AI SDK
// The SDK automatically picks up the GEMINI_API_KEY environment variable.
const ai = new GoogleGenAI({});

export interface DraftGenerationParams {
  jobTitle: string;
  customerName: string;
  typedNotes?: string;
  mediaUris: string[]; // List of gs:// URIs for photos and audio
}

export interface GeneratedDraft {
  workCompletedJson: string;
  findingsJson: string;
  recommendationsJson: string;
}

// Define the expected output schema using the standard Schema interface from @google/genai
const draftSchema: Schema = {
  type: Type.OBJECT,
  properties: {
    workCompletedJson: {
      type: Type.STRING,
      description: "A comprehensive bulleted list (separated by '||') of all work completed by the technician. E.g. 'Replaced cabinet hinge||Realigned kitchen cabinet door'."
    },
    findingsJson: {
      type: Type.STRING,
      description: "A comprehensive bulleted list (separated by '||') of any findings, observations, or issues noted. E.g. 'Minor moisture marks near the base||Old hinges were heavily rusted'."
    },
    recommendationsJson: {
      type: Type.STRING,
      description: "A comprehensive bulleted list (separated by '||') of recommended next steps for the customer. E.g. 'Monitor for moisture||Apply rust-preventative coating'."
    }
  },
  required: ["workCompletedJson", "findingsJson", "recommendationsJson"]
};

export async function generateReportDraft(params: DraftGenerationParams): Promise<GeneratedDraft> {
  const { jobTitle, customerName, typedNotes, mediaUris } = params;

  // Prepare the prompt
  let promptText = `
    You are an expert AI assistant for field service technicians.
    Your task is to generate a professional job completion report draft based on the provided inputs, which may include text notes, photos, and an audio transcription of the technician's voice notes.

    Context:
    - Customer Name: ${customerName}
    - Job Title: ${jobTitle}
    ${typedNotes ? `- Technician Typed Notes: ${typedNotes}` : ''}

    Instructions:
    1. Analyze the provided audio and images carefully to understand what work was done, what was found, and what is recommended.
    2. Do NOT hallucinate details. Only include what is explicitly mentioned in the notes/audio or clearly visible in the photos.
    3. Output the response exactly according to the requested JSON schema.
  `;

  // Prepare contents for the API
  const contents: any[] = [
    { text: promptText }
  ];

  for (const uri of mediaUris) {
    // Determine mime type heuristically based on extension
    let mimeType = 'image/jpeg';
    if (uri.endsWith('.png')) mimeType = 'image/png';
    else if (uri.endsWith('.m4a')) mimeType = 'audio/mp4';
    else if (uri.endsWith('.mp3')) mimeType = 'audio/mp3';
    else if (uri.endsWith('.mp4')) mimeType = 'video/mp4';

    contents.push({
      fileData: {
        fileUri: uri,
        mimeType: mimeType
      }
    });
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
