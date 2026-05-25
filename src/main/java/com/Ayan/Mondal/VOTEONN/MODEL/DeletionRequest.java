package com.Ayan.Mondal.VOTEONN.MODEL;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deletion_requests")
public class DeletionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String fatherName;

    @Column(nullable = false)
    private String dob;                  // stored as String (yyyy-MM-dd)

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String securityPin;

    @Column(nullable = false)
    private String voterId;

    @Column(nullable = false)
    private String deathCertificatePath; // server-side path of uploaded certificate

    @Column(nullable = false)
    private String status;               // PENDING | APPROVED | REJECTED

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // ── Constructors ──────────────────────────────────────────────────────────
    public DeletionRequest() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getFullName()                  { return fullName; }
    public void setFullName(String n)            { this.fullName = n; }

    public String getFatherName()                { return fatherName; }
    public void setFatherName(String f)          { this.fatherName = f; }

    public String getDob()                       { return dob; }
    public void setDob(String dob)               { this.dob = dob; }

    public String getEmail()                     { return email; }
    public void setEmail(String e)               { this.email = e; }

    public String getSecurityPin()               { return securityPin; }
    public void setSecurityPin(String p)         { this.securityPin = p; }

    public String getVoterId()                   { return voterId; }
    public void setVoterId(String v)             { this.voterId = v; }

    public String getDeathCertificatePath()      { return deathCertificatePath; }
    public void setDeathCertificatePath(String p){ this.deathCertificatePath = p; }

    public String getStatus()                    { return status; }
    public void setStatus(String s)              { this.status = s; }

    public LocalDateTime getSubmittedAt()        { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t)  { this.submittedAt = t; }
}