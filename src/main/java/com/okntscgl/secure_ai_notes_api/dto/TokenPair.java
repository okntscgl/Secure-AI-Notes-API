package com.okntscgl.secure_ai_notes_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
