import SwiftUI
import Speech
import AVFoundation
import shared

struct DictationView: View {
    @ObservedObject var viewModel: ReportListViewModel
    @Environment(\.presentationMode) var presentationMode

    @State private var isRecording = false
    @State private var transcriptText = ""
    @State private var titleText = ""
    @State private var jobSiteText = ""
    @State private var inspectorText = ""
    @State private var speechRecognizer = SFSpeechRecognizer(locale: Locale(identifier: "en-US"))
    @State private var recognitionTask: SFSpeechRecognitionTask?
    @State private var audioEngine = AVAudioEngine()
    @State private var generatedReport: SharedReport? = nil
    @State private var navigateToDetail = false

    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                VStack(spacing: 12) {
                    TextField("Report Title", text: $titleText)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    TextField("Job Site Address", text: $jobSiteText)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    TextField("Inspector Name", text: $inspectorText)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                }
                .padding(.horizontal)

                VStack(spacing: 8) {
                    Image(systemName: isRecording ? "mic.fill" : "mic.slash.fill")
                        .font(.system(size: 40, weight: .bold))
                        .foregroundColor(isRecording ? .red : .accentColor)
                        .scaleEffect(isRecording ? 1.1 : 1.0)
                        .animation(isRecording ? Animation.easeInOut(duration: 0.8).repeatForever(autoreverses: true) : .default, value: isRecording)

                    Text(isRecording ? "Recording Dictation..." : "Tap Microphone to Start")
                        .font(.headline)

                    Text("Speak site observations and findings clearly.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(16)

                ScrollView {
                    Text(transcriptText.isEmpty ? "Spoken notes will appear here in real-time..." : transcriptText)
                        .font(.body)
                        .foregroundColor(transcriptText.isEmpty ? .secondary : .primary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding()
                }
                .frame(maxHeight: .infinity)
                .background(Color(UIColor.systemBackground))
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(Color.secondary.opacity(0.3), lineWidth: 1)
                )

                HStack(spacing: 16) {
                    Button(action: toggleRecording) {
                        HStack {
                            Image(systemName: isRecording ? "stop.fill" : "record.circle")
                            Text(isRecording ? "Stop Dictation" : "Start Dictation")
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(isRecording ? Color.red : Color.blue)
                        .cornerRadius(12)
                    }

                    if !transcriptText.isEmpty && !isRecording {
                        Button(action: generateAIReport) {
                            HStack {
                                if viewModel.isLoading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                } else {
                                    Image(systemName: "wand.and.stars")
                                    Text("Generate Report")
                                }
                            }
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.green)
                            .cornerRadius(12)
                        }
                        .disabled(viewModel.isLoading)
                    }
                }

                if let error = viewModel.errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            }
            .padding()
            .navigationTitle("Dictate Field Report")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .background(
                NavigationLink(
                    destination: Group {
                        if let report = generatedReport {
                            ReportDetailView(report: report, viewModel: viewModel)
                        }
                    },
                    isActive: $navigateToDetail
                ) {
                    EmptyView()
                }
            )
        }
    }

    private func toggleRecording() {
        if isRecording {
            stopRecording()
        } else {
            requestSpeechPermissionAndStart()
        }
    }

    private func requestSpeechPermissionAndStart() {
        SFSpeechRecognizer.requestAuthorization { authStatus in
            DispatchQueue.main.async {
                if authStatus == .authorized {
                    self.startRecording()
                } else {
                    self.viewModel.errorMessage = "Speech recognition permission denied"
                }
            }
        }
    }

    private func startRecording() {
        transcriptText = ""
        let audioSession = AVAudioSession.sharedInstance()
        try? audioSession.setCategory(.record, mode: .measurement, options: .duckOthers)
        try? audioSession.setActive(true, options: .notifyOthersOnDeactivation)

        let request = SFSpeechAudioBufferRecognitionRequest()
        let inputNode = audioEngine.inputNode
        request.shouldReportPartialResults = true

        recognitionTask = speechRecognizer?.recognitionTask(with: request) { result, error in
            if let result = result {
                self.transcriptText = result.bestTranscription.formattedString
            }
            if error != nil || (result?.isFinal ?? false) {
                self.audioEngine.stop()
                inputNode.removeTap(onBus: 0)
            }
        }

        let recordingFormat = inputNode.outputFormat(forBus: 0)
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { buffer, _ in
            request.append(buffer)
        }

        audioEngine.prepare()
        try? audioEngine.start()
        isRecording = true
    }

    private func stopRecording() {
        audioEngine.stop()
        audioEngine.inputNode.removeTap(onBus: 0)
        recognitionTask?.cancel()
        isRecording = false
    }

    private func generateAIReport() {
        viewModel.generateReportFromAudio(
            title: titleText,
            jobSite: jobSiteText,
            inspectorName: inspectorText,
            notesText: transcriptText
        ) { report in
            if let report = report {
                self.generatedReport = report
                self.navigateToDetail = true
            }
        }
    }
}
