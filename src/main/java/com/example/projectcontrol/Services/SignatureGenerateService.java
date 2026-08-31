package com.example.projectcontrol.Services;

import java.util.Random;

public class SignatureGenerateService {

    public String generateSignature(int length) {
        Random random = new Random();

        //GENERATE A RANDOM STRING
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder generateString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());

            generateString.append(characters.charAt(index));
        }

        return generateString.toString();
    }
}
