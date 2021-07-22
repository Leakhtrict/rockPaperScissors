package com.company;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import java.io.IOException;

public class Game {
    private static final String HMAC_ALGO = "HmacSHA512";
    private static final String INPUT_ERROR = "Wrong input. Number of variants must be an odd number " +
            "that is more or equal to 3. Duplicates are not allowed.\n" +
            "Examples: \"rock paper scissors\" - correct\n" +
            "\"rock paper scissors lizard\" - wrong\n" +
            "\"rock roCK Rock\" - wrong";

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length*2);
        for(byte b: bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        int n = args.length;
        String[] variants = new String[n];
        for (int i = 0; i < n; i++) {
            variants[i] = args[i];
            for (int j = 0; j < i; j++) {
                if (variants[i].equalsIgnoreCase(variants[j])){
                    System.out.println(INPUT_ERROR);
                    return;
                }
            }
        }
        if (variants.length < 3 || variants.length % 2 == 0){
            System.out.println(INPUT_ERROR);
            return;
        }

        int compTurn = 1 + (int)(Math.random()*n);
        SecureRandom secureRandom = new SecureRandom();
        byte[] hmacKey = new byte[16];
        secureRandom.nextBytes(hmacKey);

        Mac signer = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(hmacKey, HMAC_ALGO);
        signer.init(keySpec);
        byte[] hmac = signer.doFinal(Integer.toString(compTurn).getBytes("utf-8"));
        System.out.println("HMAC: " + bytesToHex(hmac));

        Scanner in = new Scanner(System.in);
        int playerTurn = -1;
        do{
            System.out.println("Available moves: ");
            for (int i = 0; i < n + 1; i++){
                if(i == n){
                    System.out.println("0 - exit");
                    break;
                }
                System.out.println((i+1) + " - " + variants[i]);
            }
            System.out.print("Enter your move: ");
            if(in.hasNextInt())
                playerTurn = in.nextInt();
            else
                in.next();
            if(playerTurn == 0)
                return;
            if (playerTurn < 0 || playerTurn > n) {
                System.out.println("--------------------");
            }
        } while(playerTurn < 0 || playerTurn > n);

        in.close();
        System.out.println("Your move: " + variants[playerTurn-1]);
        System.out.println("Computer move: " + variants[compTurn-1]);

        if (playerTurn == compTurn)
            System.out.println("A tie!");
        else {
            int half = n/2;
            boolean check = true;
            for (int i = 0; i < half; i++){
                if (compTurn - 1 == (playerTurn + i) % n){
                    System.out.println("You lose!");
                    check = false;
                    break;
                }
            }
            if (check)
                System.out.println("You win!");
        }

        System.out.println("HMAC key: " + bytesToHex(hmacKey));
    }
}
