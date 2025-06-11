import { CommonModule } from '@angular/common';

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AiService } from '../../../../services/services/ai.service';
import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-botui',
  imports: [FormsModule,CommonModule],
  templateUrl: './botui.component.html',
  styleUrl: './botui.component.css'
})
export class BotuiComponent implements OnInit {


 @Output() onTranscript = new EventEmitter<any>();
 public correspondence: string = "Initializing...";

  public started: boolean = false;
  public question: string = "";
  public response: string = "";
  public isWebRTCActive: boolean = false;
   peerConnection: RTCPeerConnection | null = null;
   dataChannel: RTCDataChannel | null = null;
   recognition: any = null;
  constructor(private aiService: AiService) {
    this.initializeWebRTC();
  }

  private initializeWebRTC(): void {
    this.isWebRTCActive = false;
    this.correspondence = "Initializing WebRTC...";
    const configuration: RTCConfiguration = {
      iceServers: [{ urls: "stun:stun.l.google.com:19302" }]
    };
    this.peerConnection = new RTCPeerConnection(configuration);
  }

  ngOnInit(): void {}

  startWebRTC(): void {
    if (this.isWebRTCActive) {
      this.correspondence = "WebRTC already active. Stop WebRTC to restart.";
      return;
    }

    if (!this.peerConnection) {
      this.correspondence = "Error: WebRTC not initialized.";
      return;
    }

    this.peerConnection.ontrack = this.handleTrack;
    this.peerConnection.onicecandidate = this.handleIceCandidate;

    this.dataChannel = this.peerConnection.createDataChannel('chat');

    navigator.mediaDevices.getUserMedia({ audio: true }).then((stream) => {
      stream.getTracks().forEach(track => {
        console.log("Audio track detected:", track);
        this.peerConnection?.addTrack(track, stream);
      });
      this.isWebRTCActive = true;
      this.correspondence = "WebRTC started. Say a command...";
      this.startSpeechRecognition();
    }).catch(error => {
      this.correspondence = `Error accessing microphone: ${error.message}`;
      console.error("Media error:", error);
    });
  }

  stopWebRTC(): void {
    if (this.recognition) {
      this.recognition.stop();
      this.recognition = null;
    }
    if (this.peerConnection) {
      this.peerConnection.close();
      this.peerConnection = null;
    }
    if (this.dataChannel) {
      this.dataChannel.close();
      this.dataChannel = null;
    }
    this.isWebRTCActive = false;
    this.response = "";
    this.correspondence = "WebRTC stopped. Click 'Start WebRTC' to begin again.";
    this.initializeWebRTC();
  }

  private handleTrack(event: RTCTrackEvent): void {
    console.log("Received track:", event);
  }

  private handleIceCandidate(event: RTCPeerConnectionIceEvent): void {
    if (event.candidate) {
      console.log("ICE candidate:", event.candidate);
    }
  }

  private processCommand(command: string): void {
    console.log("Processing command:", command);
    this.correspondence = "Sending command to backend...";

    this.aiService.askAgent(command).subscribe({
      next: (event: HttpEvent<any>) => {
        if (event.type === HttpEventType.Response) {
          this.response = event.body; // The actual response from the backend
          this.correspondence = this.response;
          console.log("Backend response:", this.response);
          if (this.recognition) {
            this.recognition.stop();
          }
        }
      },
      error: (error) => {
        this.correspondence = `Error communicating with backend: ${error.message}`;
        console.error("Backend error:", error);
      }
    });
  }

  askAgent(): void {
    if (this.question.trim()) {
      this.processCommand(this.question);
      this.question = "";
    } else {
      this.correspondence = "Please enter a command.";
    }
  }

  startSpeechRecognition(): void {
    if (!('webkitSpeechRecognition' in window || 'SpeechRecognition' in window)) {
      this.correspondence = "Speech recognition not supported in this browser.";
      console.error("Speech recognition not supported.");
      return;
    }

    const SpeechRecognition = (window as any).webkitSpeechRecognition || (window as any).SpeechRecognition;
    this.recognition = new SpeechRecognition();

    this.recognition.lang = 'fr-FR';
    this.recognition.continuous = true;
    this.recognition.interimResults = false;

    this.recognition.onstart = () => {
      this.correspondence = "Speech recognition started. Speak now...";
      console.log("Speech recognition started.");
    };

    this.recognition.onresult = (event: any) => {
      const command = event.results[event.results.length - 1][0].transcript;
      this.correspondence = `Heard: ${command}`;
      console.log("Speech recognition result:", command);
      this.processCommand(command);
    };

    this.recognition.onerror = (event: any) => {
      this.correspondence = `Speech recognition error: ${event.error} - ${event.message || 'No message'}`;
      console.error("Speech recognition error:", event.error, event.message);
      if (event.error === 'no-speech') {
        this.correspondence = "No speech detected. Please speak louder or closer to the microphone.";
      } else if (event.error === 'not-allowed' || event.error === 'permission-denied') {
        this.correspondence = "Microphone access denied. Please allow microphone access in browser settings.";
      }
    };

    this.recognition.onend = () => {
      this.correspondence = "Speech recognition stopped. Click 'Start WebRTC' to try again.";
      console.log("Speech recognition ended.");
      this.recognition = null;
    };

    try {
      this.recognition.start();
      console.log("Attempting to start speech recognition...");
      setTimeout(() => {
        if (this.recognition && this.recognition.state === 'recording') {
          this.correspondence = "Timeout: No command detected. Stopping recognition.";
          this.recognition.stop();
        }
      }, 10000);
    } catch (error) {
      this.correspondence = `Error starting speech recognition: ${error}`;
      console.error("Speech recognition start error:", error);
    }
  }
}