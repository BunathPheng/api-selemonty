package org.hrd.finalprojectmuseum.utils;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.service.BookingService;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UniqueTextCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SEGMENT_LENGTH = 4;
    private static final int TOTAL_SEGMENTS = 2;
    private static final int MAX_ATTEMPTS = 100;

    private final SecureRandom random = new SecureRandom();

    private final BookingRepository bookingRepository;

    /**
     * Generate unique text code for ticket - ONE METHOD TO RULE THEM ALL
     * @param ticketId The ticket ID to associate with the code
     * @return Unique text code in format AB12-CD34
     */
    public String generateUniqueTextCode() {
        String textCode;
        int attempts = 0;

        do {
            textCode = generateCode();
            attempts++;

            if (attempts > MAX_ATTEMPTS) {
                throw new RuntimeException("Unable to generate unique text code after " + MAX_ATTEMPTS + " attempts");
            }
        } while (isCodeExists(textCode));

        // Optional: Save the mapping if you want to track it separately
        // saveCodeMapping(textCode, ticketId);

        return textCode;
    }

    /**
     * Generate multiple unique codes at once
     * @param count Number of unique codes needed
     * @return List of unique text codes
     */
    public List<String> generateMultipleUniqueCodes(int count) {
        List<String> codes = new ArrayList<>();
        Set<String> generatedCodes = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String code;
            int attempts = 0;

            do {
                code = generateCode();
                attempts++;

                if (attempts > MAX_ATTEMPTS) {
                    throw new RuntimeException("Unable to generate unique code #" + (i + 1) + " after " + MAX_ATTEMPTS + " attempts");
                }
            } while (isCodeExists(code) || generatedCodes.contains(code));

            codes.add(code);
            generatedCodes.add(code);
        }

        return codes;
    }

    /**
     * Validate if a text code format is correct
     * @param code Code to validate
     * @return true if format is valid
     */
    public boolean isValidFormat(String code) {
        if (code == null || code.length() != 9) {
            return false;
        }
        return code.matches("^[A-Z0-9]{4}-[A-Z0-9]{4}$");
    }

    /**
     * Check if code exists in database
     * @param code Code to check
     * @return true if code already exists
     */
    public boolean isCodeExists(String code) {
        return bookingRepository.existsByTextCode(code);
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Generate basic code in AB12-CD34 format
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();

        for (int segment = 0; segment < TOTAL_SEGMENTS; segment++) {
            if (segment > 0) {
                code.append("-");
            }

            for (int i = 0; i < SEGMENT_LENGTH; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                code.append(CHARACTERS.charAt(randomIndex));
            }
        }

        return code.toString();
    }

    /**
     * Optional: Save code mapping for tracking (if needed)
     */
    private void saveCodeMapping(String code, Long ticketId) {
        // Implement if you want to track code generation separately
        // TextCodeMapping mapping = new TextCodeMapping(code, ticketId, LocalDateTime.now());
        // textCodeMappingRepository.save(mapping);
    }
}