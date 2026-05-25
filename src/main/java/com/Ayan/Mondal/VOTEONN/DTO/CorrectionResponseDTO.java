package com.Ayan.Mondal.VOTEONN.DTO;

/**
 * DTO for the Correction Request response sent back to the frontend.
 * The request itself arrives as multipart/form-data (handled in the Controller),
 * so we only need a response DTO here.
 */
public class CorrectionResponseDTO {

    private Long requestId;
    private String status;
    private String message;

    public CorrectionResponseDTO() {}

    public CorrectionResponseDTO(Long requestId, String status, String message) {
        this.requestId = requestId;
        this.status    = status;
        this.message   = message;
    }

    public Long   getRequestId()             { return requestId; }
    public void   setRequestId(Long id)      { this.requestId = id; }

    public String getStatus()                { return status; }
    public void   setStatus(String status)   { this.status = status; }

    public String getMessage()               { return message; }
    public void   setMessage(String message) { this.message = message; }
}