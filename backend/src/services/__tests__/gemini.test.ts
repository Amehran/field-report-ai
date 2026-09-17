import { describe, it, expect, vi, beforeEach } from 'vitest';
import { generateReportDraft } from '../gemini';
import { GoogleGenAI } from '@google/genai';

// Mock the GoogleGenAI module
const { mockGenerateContent } = vi.hoisted(() => {
  return { mockGenerateContent: vi.fn() };
});

vi.mock('@google/genai', () => {
  return {
    GoogleGenAI: vi.fn().mockImplementation(() => {
      return {
        models: {
          generateContent: mockGenerateContent
        }
      };
    }),
    Type: { OBJECT: 'OBJECT', STRING: 'STRING' },
    Schema: {}
  };
});

describe('Gemini Service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should successfully parse a valid draft generation response', async () => {
    // Setup mock response
    
    const mockDraft = {
      workCompletedJson: "Fixed hinge||Cleaned area",
      findingsJson: "Hinge was rusty",
      recommendationsJson: "Use oil"
    };

    mockGenerateContent.mockResolvedValue({
      text: JSON.stringify(mockDraft)
    });

    const result = await generateReportDraft({
      jobTitle: "Fix Door",
      customerName: "John Doe",
      mediaUris: ["gs://bucket/photo.jpg", "gs://bucket/audio.m4a"]
    });

    expect(result).toEqual(mockDraft);
    expect(mockGenerateContent).toHaveBeenCalledTimes(1);
    
    // Verify contents formatting
    const callArgs = mockGenerateContent.mock.calls[0][0];
    expect(callArgs.model).toBe('gemini-2.5-flash');
    expect(callArgs.contents.length).toBe(3); // 1 text + 2 media
    expect(callArgs.contents[1].fileData.mimeType).toBe('image/jpeg');
    expect(callArgs.contents[2].fileData.mimeType).toBe('audio/mp4');
  });

  it('should throw an error if Gemini returns an empty response', async () => {
    mockGenerateContent.mockResolvedValue({ text: null });

    await expect(generateReportDraft({
      jobTitle: "Fix Door",
      customerName: "John Doe",
      mediaUris: []
    })).rejects.toThrow('Failed to generate report draft.');
  });
});
