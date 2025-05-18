package org.hrd.finalprojectmuseum.model.entity;
// Importing required classes
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
// Class
@Builder
public class EmailDetails {
    // Class data members
    private String recipient;
    private String plainTextBody;
    private Boolean isHtml;
    private String msgBody;
    private String subject;
    private String attachment;
}
