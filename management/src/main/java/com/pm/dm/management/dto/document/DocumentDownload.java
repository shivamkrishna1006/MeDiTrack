package com.pm.dm.management.dto.document;

import org.springframework.core.io.Resource;

public class DocumentDownload {
    private Resource resource;
    private String filename;
    private String contentType;

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
