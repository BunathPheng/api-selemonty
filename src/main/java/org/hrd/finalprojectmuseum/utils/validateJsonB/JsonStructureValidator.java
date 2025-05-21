package org.hrd.finalprojectmuseum.utils.validateJsonB;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class JsonStructureValidator implements ConstraintValidator<ValidJson, JSONObject> {

    private String[] requiredFields;
    private int maxSize;

    // Pattern for validating URLs - must start with http:// or https://
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*$");

    // List of allowed file extensions
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".glb");

    @Override
    public void initialize(ValidJson constraintAnnotation) {
        this.requiredFields = constraintAnnotation.requiredFields();
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(JSONObject value, ConstraintValidatorContext context) {
        // Skip validation if null
        if (value == null) {
            return true;
        }

        // Completely disable the default constraint violation
        context.disableDefaultConstraintViolation();

        // Initialize validity flag
        boolean valid = true;

        try {
            // Check JSON size
            if (value.toString().length() > maxSize) {
                context.buildConstraintViolationWithTemplate(
                                "JSON size exceeds maximum allowed size of " + maxSize + " characters")
                        .addConstraintViolation();
                valid = false;
            }

            // Check required fields
            for (String field : requiredFields) {
                if (!value.containsKey(field) || value.get(field) == null) {
                    context.buildConstraintViolationWithTemplate(
                                    "Required field '" + field + "' is missing or null")
                            .addConstraintViolation();
                    valid = false;
                }
            }

            // Recursively validate all values
            valid = validateJsonStructure(value, context, "") && valid;

            // Check if we found at least one valid image URL
            if (!hasFoundValidImageUrl(value)) {
                context.buildConstraintViolationWithTemplate(
                                "JSON must contain at least one valid image URL. URLs must start with http:// or https:// and end with .jpg, .jpeg, .png, .gif, or .glb")
                        .addConstraintViolation();
                valid = false;
            }

            return valid;
        } catch (Exception e) {
            // If any exception occurs during validation, report it
            context.buildConstraintViolationWithTemplate(
                            "Error validating JSON: " + e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }

    private boolean validateJsonStructure(Object value, ConstraintValidatorContext context, String path) {
        boolean valid = true;

        if (value instanceof String) {
            // If it's a string, it should be a valid image URL
            String url = (String) value;
            if (!url.isEmpty() && !isValidImageUrl(url)) {
                context.buildConstraintViolationWithTemplate(
                                "Invalid image URL format: " + url +
                                        ". URLs must start with http:// or https:// and end with .jpg, .jpeg, .png, .gif, or .glb")
                        .addConstraintViolation();
                valid = false;
            }
        } else if (value instanceof JSONObject) {
            // If it's a JSON object, validate all its values
            JSONObject jsonObject = (JSONObject) value;

            // Check for empty objects with no values
            if (jsonObject.isEmpty()) {
                return true; // Empty objects are valid
            }

            // Reject objects with "empty": false but no valid URLs
            if (jsonObject.containsKey("empty") && jsonObject.getBooleanValue("empty") == false &&
                    jsonObject.size() == 1) {
                context.buildConstraintViolationWithTemplate(
                                "JSON object with 'empty: false' must contain image URLs")
                        .addConstraintViolation();
                valid = false;
            }

            // Check for additionalProp pattern which is not allowed
            for (String key : jsonObject.keySet()) {
                if (key.startsWith("additionalProp")) {
                    context.buildConstraintViolationWithTemplate(
                                    "Invalid property name: '" + key + "'. Properties must be meaningful names, not placeholders")
                            .addConstraintViolation();
                    valid = false;
                }

                // Recursively validate all values
                String newPath = path.isEmpty() ? key : path + "." + key;
                valid = validateJsonStructure(jsonObject.get(key), context, newPath) && valid;
            }
        } else if (value instanceof List) {
            // If it's a standard List
            List<?> list = (List<?>) value;
            for (int i = 0; i < list.size(); i++) {
                String newPath = path + "[" + i + "]";
                valid = validateJsonStructure(list.get(i), context, newPath) && valid;
            }
        } else if (value instanceof JSONArray) {
            // If it's a FastJSON2 JSONArray
            JSONArray jsonArray = (JSONArray) value;
            for (int i = 0; i < jsonArray.size(); i++) {
                String newPath = path + "[" + i + "]";
                valid = validateJsonStructure(jsonArray.get(i), context, newPath) && valid;
            }
        } else if (value != null && !(value instanceof Boolean) && !(value instanceof Number)) {
            // Unknown type that's not a primitive
            context.buildConstraintViolationWithTemplate(
                            "Invalid value type at " + (path.isEmpty() ? "root" : path) +
                                    ": Only strings, objects, arrays, and primitives are allowed")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }

    private boolean hasFoundValidImageUrl(Object value) {
        if (value instanceof String) {
            return isValidImageUrl((String) value);
        } else if (value instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) value;
            for (Object val : jsonObject.values()) {
                if (hasFoundValidImageUrl(val)) {
                    return true;
                }
            }
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object item : list) {
                if (hasFoundValidImageUrl(item)) {
                    return true;
                }
            }
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            for (int i = 0; i < jsonArray.size(); i++) {
                if (hasFoundValidImageUrl(jsonArray.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidImageUrl(String url) {
        // Check URL format (http:// or https://)
        if (!URL_PATTERN.matcher(url).matches()) {
            return false;
        }

        // Check file extension
        return ALLOWED_EXTENSIONS.stream()
                .anyMatch(ext -> url.toLowerCase().endsWith(ext));
    }
}