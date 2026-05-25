package com.Ayan.Mondal.VOTEONN.MODEL;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "correction_requests")
public class CorrectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String voterId;

    @Column(nullable = false)
    private String securityPin;          // current PIN for identity check

    @Column(nullable = false)
    private String fieldToCorrect;       // "name" | "father" | "dob" | "email" | "pin"

    @Column(nullable = false)
    private String currentValue;         // value the voter claims is wrong

    @Column(nullable = false)
    private String newValue;             // value they want to set

    @Column(nullable = false)
    private String documentPath;         // server-side path of the uploaded document

    @Column(nullable = false)
    private String status;               // PENDING | APPROVED | REJECTED

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // ── Constructors ──────────────────────────────────────────────────────────
    public CorrectionRequest() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getVoterId()                 { return voterId; }
    public void setVoterId(String voterId)     { this.voterId = voterId; }

    public String getSecurityPin()             { return securityPin; }
    public void setSecurityPin(String p)       { this.securityPin = p; }

    public String getFieldToCorrect()          { return fieldToCorrect; }
    public void setFieldToCorrect(String f)    { this.fieldToCorrect = f; }

    public String getCurrentValue()            { return currentValue; }
    public void setCurrentValue(String v)      { this.currentValue = v; }

    public String getNewValue()                { return newValue; }
    public void setNewValue(String v)          { this.newValue = v; }

    public String getDocumentPath()            { return documentPath; }
    public void setDocumentPath(String p)      { this.documentPath = p; }

    public String getStatus()                  { return status; }
    public void setStatus(String status)       { this.status = status; }

    public LocalDateTime getSubmittedAt()      { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t){ this.submittedAt = t; }
}