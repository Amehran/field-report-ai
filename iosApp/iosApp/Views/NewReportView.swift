import SwiftUI
import Speech
import AVFoundation
import PhotosUI
import shared

struct NewReportView: View {
    @ObservedObject var viewModel: ReportListViewModel
    @Environment(\.presentationMode) var presentationMode

    @State private var isRecording = false
    @State private var transcriptText = ""
    @State private var speechRecognizer = SFSpeechRecognizer(locale: Locale(identifier: "en-US"))
    @State private var recognitionTask: SFSpeechRecognitionTask?
    @State private var audioEngine = AVAudioEngine()

    // Photo picker state
    @State private var showCameraPicker = false
    @State private var showPhotoSourceDialog = false

    // Navigation state
    @State private var navigateToReview = false
    @State private var createdReport: SharedReport? = nil

    private let tealColor = Color(red: 15/255, green: 118/255, blue: 110/255)
    private let lightTealPill = Color(red: 243/255, green: 232/255, blue: 255/255)

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Date & Customer/Job Inputs Card
                    VStack(alignment: .leading, spacing: 14) {
                        Text("Report Date: \(formattedCurrentDate())")
                            .font(.caption)
                            .bold()
                            .foregroundColor(.secondary)

                        VStack(spacing: 12) {
                            HStack {
                                TextField("Customer Name", text: $viewModel.customerName)
                                if !viewModel.customerName.isEmpty {
                                    Button(action: { viewModel.customerName = "" }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundColor(.gray)
                                    }
                                }
                            }
                            .padding()
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )

                            HStack {
                                TextField("Job Name", text: $viewModel.jobName)
                                if !viewModel.jobName.isEmpty {
                                    Button(action: { viewModel.jobName = "" }) {
                                        Image(systemName: "xmark.circle.fill")
                                            .foregroundColor(.gray)
                                    }
                                }
                            }
                            .padding()
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )
                        }

                        // AI Report Tone Selector
                        VStack(alignment: .leading, spacing: 8) {
                            Text("AI Report Tone")
                                .font(.subheadline)
                                .bold()
                                .foregroundColor(.primary)

                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 8) {
                                    ForEach(AiTone.allCases) { tone in
                                        Button(action: {
                                            viewModel.selectedTone = tone
                                        }) {
                                            HStack(spacing: 6) {
                                                if viewModel.selectedTone == tone {
                                                    Image(systemName: "checkmark")
                                                        .font(.caption)
                                                        .bold()
                                                }
                                                Text(tone.rawValue)
                                                    .font(.subheadline)
                                                    .fontWeight(viewModel.selectedTone == tone ? .semibold : .regular)
                                            }
                                            .padding(.horizontal, 14)
                                            .padding(.vertical, 10)
                                            .background(viewModel.selectedTone == tone ? lightTealPill : Color(UIColor.systemBackground))
                                            .foregroundColor(viewModel.selectedTone == tone ? .purple : .primary)
                                            .cornerRadius(20)
                                            .overlay(
                                                RoundedRectangle(cornerRadius: 20)
                                                    .stroke(viewModel.selectedTone == tone ? Color.purple.opacity(0.4) : Color.gray.opacity(0.2), lineWidth: 1)
                                            )
                                        }
                                    }
                                }
                            }

                            Text(viewModel.selectedTone.description)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(16)

                    // Photos Section
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("Photos")
                                .font(.headline)
                                .bold()

                            Spacer()

                            Button(action: {
                                showPhotoSourceDialog = true
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "plus")
                                    Text("Add photo")
                                }
                                .font(.subheadline)
                                .bold()
                                .foregroundColor(tealColor)
                            }
                        }

                        if viewModel.capturedImages.isEmpty {
                            HStack(spacing: 12) {
                                PhotoPlaceholderCard(tag: "BEFORE") {
                                    showPhotoSourceDialog = true
                                }
                                PhotoPlaceholderCard(tag: "AFTER") {
                                    showPhotoSourceDialog = true
                                }
                            }
                        } else {
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
                                    ForEach(Array(viewModel.capturedImages.enumerated()), id: \.element.id) { index, item in
                                        ZStack(alignment: .topTrailing) {
                                            VStack(spacing: 4) {
                                                Image(uiImage: item.image)
                                                    .resizable()
                                                    .scaledToFill()
                                                    .frame(width: 100, height: 110)
                                                    .cornerRadius(12)
                                                    .clipped()
                                                    .onTapGesture {
                                                        // Cycle tag on tap matching Android onToggleLabel()
                                                        let tags = PhotoTag.allCases
                                                        if let currentIndex = tags.firstIndex(of: item.tag) {
                                                            let nextIndex = (currentIndex + 1) % tags.count
                                                            viewModel.capturedImages[index].tag = tags[nextIndex]
                                                        }
                                                    }

                                                Text(item.tag.rawValue)
                                                    .font(.caption2)
                                                    .bold()
                                                    .padding(.horizontal, 8)
                                                    .padding(.vertical, 4)
                                                    .background(Color.teal.opacity(0.2))
                                                    .foregroundColor(.teal)
                                                    .cornerRadius(6)
                                            }

                                            Button(action: {
                                                viewModel.capturedImages.remove(at: index)
                                            }) {
                                                Image(systemName: "xmark.circle.fill")
                                                    .foregroundColor(.red)
                                                    .background(Color.white.clipShape(Circle()))
                                            }
                                            .offset(x: 6, y: -6)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground).opacity(0.6))
                    .cornerRadius(16)

                    // Describe the Work Section
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Describe the work")
                            .font(.headline)
                            .bold()

                        // Voice Note vs Type Notes Selector
                        HStack(spacing: 0) {
                            Button(action: { viewModel.isVoiceMode = true }) {
                                HStack {
                                    if viewModel.isVoiceMode {
                                        Image(systemName: "checkmark")
                                    }
                                    Image(systemName: "waveform.and.mic")
                                    Text("Voice Note")
                                }
                                .font(.subheadline)
                                .fontWeight(viewModel.isVoiceMode ? .bold : .regular)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .background(viewModel.isVoiceMode ? lightTealPill : Color.clear)
                                .foregroundColor(viewModel.isVoiceMode ? .purple : .primary)
                            }

                            Divider()
                                .frame(height: 24)

                            Button(action: { viewModel.isVoiceMode = false }) {
                                HStack {
                                    if !viewModel.isVoiceMode {
                                        Image(systemName: "checkmark")
                                    }
                                    Image(systemName: "pencil")
                                    Text("Type Notes")
                                }
                                .font(.subheadline)
                                .fontWeight(!viewModel.isVoiceMode ? .bold : .regular)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .background(!viewModel.isVoiceMode ? lightTealPill : Color.clear)
                                .foregroundColor(!viewModel.isVoiceMode ? .purple : .primary)
                            }
                        }
                        .background(Color(UIColor.systemBackground))
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                        )

                        if viewModel.isVoiceMode {
                            // Voice recorder card
                            VStack(spacing: 12) {
                                HStack(spacing: 14) {
                                    Button(action: toggleRecording) {
                                        ZStack {
                                            Circle()
                                                .fill(isRecording ? Color.red.opacity(0.2) : Color.teal.opacity(0.1))
                                                .frame(width: 48, height: 48)

                                            Image(systemName: isRecording ? "stop.fill" : "mic.fill")
                                                .font(.title3)
                                                .foregroundColor(isRecording ? .red : tealColor)
                                        }
                                    }

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(isRecording ? "Recording voice note..." : "Record a voice note")
                                            .font(.headline)
                                        Text(isRecording ? "Tap stop when finished speaking" : "Tap to open voice recorder")
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }

                                    Spacer()
                                }

                                if !transcriptText.isEmpty {
                                    Text(transcriptText)
                                        .font(.subheadline)
                                        .foregroundColor(.primary)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                        .padding(10)
                                        .background(Color(UIColor.systemBackground))
                                        .cornerRadius(8)
                                }
                            }
                            .padding(14)
                            .background(Color(UIColor.systemBackground))
                            .cornerRadius(14)
                            .overlay(
                                RoundedRectangle(cornerRadius: 14)
                                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                            )
                        } else {
                            // Text Editor for Manual Typing
                            VStack(alignment: .trailing, spacing: 4) {
                                TextEditor(text: $viewModel.typedNotes)
                                    .frame(height: 120)
                                    .padding(8)
                                    .background(Color(UIColor.systemBackground))
                                    .cornerRadius(12)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                                    )

                                if !viewModel.typedNotes.isEmpty {
                                    Button(action: { viewModel.typedNotes = "" }) {
                                        Text("Clear notes")
                                            .font(.caption)
                                            .foregroundColor(.red)
                                    }
                                }
                            }
                        }
                    }

                    // Pricing Section
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Pricing (Optional)")
                            .font(.headline)
                            .bold()

                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Labor Cost")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                TextField("0.00", text: $viewModel.laborCost)
                                    .keyboardType(.decimalPad)
                                    .onChange(of: viewModel.laborCost) { _ in calculateTotalCost() }
                                    .padding()
                                    .background(Color(UIColor.systemBackground))
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                                    )
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Parts Cost")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                TextField("0.00", text: $viewModel.materialsCost)
                                    .keyboardType(.decimalPad)
                                    .onChange(of: viewModel.materialsCost) { _ in calculateTotalCost() }
                                    .padding()
                                    .background(Color(UIColor.systemBackground))
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                                    )
                            }
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text("Total Cost")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            TextField("0.00", text: $viewModel.totalCost)
                                .keyboardType(.decimalPad)
                                .padding()
                                .background(Color(UIColor.systemBackground))
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                                )
                        }
                    }

                    // Bottom Action Button: Generate report
                    Button(action: generateReportClicked) {
                        HStack {
                            if viewModel.isLoading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            } else {
                                Text("Generate report")
                                    .font(.headline)
                                    .bold()
                            }
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(tealColor)
                        .cornerRadius(14)
                    }
                    .disabled(viewModel.isLoading)
                    .padding(.top, 8)
                }
                .padding(16)
            }
            .navigationTitle("New Field Report")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .confirmationDialog("Add Photo", isPresented: $showPhotoSourceDialog) {
                Button("📷 Camera") {
                    showCameraPicker = true
                }
                Button("🖼️ Gallery") {
                    showCameraPicker = true
                }
                Button("Cancel", role: .cancel) {}
            }
            .sheet(isPresented: $showCameraPicker) {
                CameraPicker { image in
                    let newImage = IdentifiableImage(image: image, tag: .before)
                    viewModel.capturedImages.append(newImage)
                }
            }
            .background(
                NavigationLink(
                    destination: Group {
                        if let report = createdReport {
                            ReviewReportView(report: report, viewModel: viewModel)
                        }
                    },
                    isActive: $navigateToReview
                ) {
                    EmptyView()
                }
            )
        }
    }

    private func calculateTotalCost() {
        let labor = Double(viewModel.laborCost) ?? 0.0
        let parts = Double(viewModel.materialsCost) ?? 0.0
        let total = labor + parts
        if total > 0 {
            viewModel.totalCost = String(format: "%.2f", total)
        }
    }



    private func formattedCurrentDate() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM d, yyyy • h:mm a"
        return formatter.string(from: Date())
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

    private func generateReportClicked() {
        if isRecording {
            stopRecording()
        }

        viewModel.generateDraftReport(dictatedNotes: transcriptText) { report in
            self.createdReport = report
            self.navigateToReview = true
        }
    }
}

struct PhotoPlaceholderCard: View {
    let tag: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading) {
                Text(tag)
                    .font(.caption2)
                    .bold()
                    .padding(.horizontal, 6)
                    .padding(.vertical, 3)
                    .background(tag == "BEFORE" ? Color.gray.opacity(0.2) : Color.teal.opacity(0.2))
                    .foregroundColor(tag == "BEFORE" ? .gray : .teal)
                    .cornerRadius(4)
                    .padding(8)

                Spacer()
            }
            .frame(width: 90, height: 100)
            .background(Color(UIColor.systemBackground))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.gray.opacity(0.2), lineWidth: 1)
            )
        }
    }
}
