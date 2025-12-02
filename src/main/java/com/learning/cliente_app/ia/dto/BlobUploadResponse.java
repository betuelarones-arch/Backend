package com.learning.cliente_app.ia.dto;

public class BlobUploadResponse {
    public String message;
    public Long entityId;
    public Long fileSize;
    public String contentType;

    public BlobUploadResponse(String message, Long entityId, Long fileSize, String contentType) {
        this.message = message;
        this.entityId = entityId;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public String getMessage() { return message; }
    public Long getEntityId() { return entityId; }
    public Long getFileSize() { return fileSize; }
    public String getContentType() { return contentType; }
}