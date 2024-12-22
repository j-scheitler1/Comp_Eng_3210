import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Main {
  // Creates a mapping between condition codes and their string representations
  public static Map<Integer, String> makeConditionMap() {
    HashMap<Integer, String> conditionMap = new HashMap<>();

    // Add condition codes to the map
    conditionMap.put(0x0, "EQ"); // Equal
    conditionMap.put(0x1, "NE"); // Not Equal
    conditionMap.put(0x2, "HS"); // Unsigned Higher or Same
    conditionMap.put(0x3, "LO"); // Unsigned Lower
    conditionMap.put(0x4, "MI"); // Negative
    conditionMap.put(0x5, "PL"); // Positive or Zero
    conditionMap.put(0x6, "VS"); // Overflow
    conditionMap.put(0x7, "VC"); // No Overflow
    conditionMap.put(0x8, "HI"); // Unsigned Higher
    conditionMap.put(0x9, "LS"); // Unsigned Lower or Same
    conditionMap.put(0xa, "GE"); // Signed Greater or Equal
    conditionMap.put(0xb, "LT"); // Signed Less Than
    conditionMap.put(0xc, "GT"); // Signed Greater Than
    conditionMap.put(0xd, "LE"); // Signed Less or Equal 

    return conditionMap;
  }  

  // Creates a mapping between instruction opcodes and their string representations
  public static Map<Integer, String> makeInstructionMap() {
    Map<Integer, String> instructionMap = new HashMap<>();

    // Add opcode-to-instruction mappings
    instructionMap.put(0b10001011000, "ADD");
    instructionMap.put(0b1001000100, "ADDI");
    instructionMap.put(0b10001010000, "AND");
    instructionMap.put(0b1001001000, "ANDI");
    instructionMap.put(0b000101, "B");
    instructionMap.put(0b100101, "BL");
    instructionMap.put(0b11010110000, "BR");
    instructionMap.put(0b10110101, "CBNZ");
    instructionMap.put(0b10110100, "CBZ");
    instructionMap.put(0b11001010000, "EOR");
    instructionMap.put(0b1101001000, "EORI");
    instructionMap.put(0b11111111111, "HALT");
    instructionMap.put(0b11111000010, "LDUR");
    instructionMap.put(0b11010011011, "LSL");
    instructionMap.put(0b11010011010, "LSR");
    instructionMap.put(0b10011011000, "MUL");
    instructionMap.put(0b10101010000, "ORR");
    instructionMap.put(0b1011001000, "ORRI");
    instructionMap.put(0b11111111100, "PRNL");
    instructionMap.put(0b11111111101, "PRNT");
    instructionMap.put(0b11111000000, "STUR");
    instructionMap.put(0b11001011000, "SUB");
    instructionMap.put(0b1101000100, "SUBI");
    instructionMap.put(0b1111000100, "SUBIS");
    instructionMap.put(0b11101011000, "SUBS");
    instructionMap.put(0b11111111110, "DUMP");
    instructionMap.put(0b01010100, "B.");
    instructionMap.put(0b11111111001, "TIME");
    instructionMap.put(0b1111001000, "ANDIS");

    return instructionMap;
  }

  public static void main(String[] args) throws IOException {
    // Generate mappings for instructions and conditions
    Map<Integer, String> instructionMap = makeInstructionMap();
    Map<Integer, String> conditionMap = makeConditionMap();
    ArrayList<String> strToPrint = new ArrayList<>();

    // Maps line numbers to label numbers
    Map<Integer, Integer> labelMap = new HashMap<>();
    int labelCount = 0;

    // Determine file path based on program arguments
    Path path;
    if(args.length >= 1) {
      path = Paths.get(args[0]); // Use provided path
    } else {
      path = Paths.get("./assignment1.legv8asm.machine"); // Default path
    }

    // Read the file's contents as bytes
    byte[] fileContents = Files.readAllBytes(path);
    int[] instructions = new int[fileContents.length / 4];

    // Convert the bytes into 32-bit instructions
    for (int i = 0; i < fileContents.length; i += 4) {
      instructions[i / 4] = ((fileContents[i]) << 24 & 0xFF000000) |
                            ((fileContents[i + 1] ) << 16 & 0xFF0000) |
                            ((fileContents[i + 2] ) << 8 & 0xFF00) |
                            (fileContents[i + 3] & 0xFF);
    }

    // Parse each instruction
    for(int i = 0; i < instructions.length; i++) {
      InstructionData instruction = new InstructionData(instructions[i]);

              // Handle 6-bit opcode instructions
      if(instructionMap.containsKey(instruction.op_6)) {
        String instName = instructionMap.get(instruction.op_6);
        int convertedBRAddress = convertTo2s(instruction.BR_address, 26);
        if(!labelMap.containsKey(i+convertedBRAddress)) {
          labelCount++;
          labelMap.put(i+convertedBRAddress, labelCount );
        }
        strToPrint.add(instName + " label_" + labelMap.get(i+convertedBRAddress ));
      }

    

      else if (instructionMap.containsKey(instruction.op_8)) {
        // Handle 8-bit opcode instructions (e.g., CB-type)
        String instName;
        int convertedC_BR_Address = convertTo2s(instruction.C_BR_address, 19);
        if(instruction.op_8 == 0b01010100) {
          // Conditional branch instruction
          instName = "B." + conditionMap.get(instruction.rd);
          if(!labelMap.containsKey(i + convertedC_BR_Address)) {
            labelCount++;
            labelMap.put(i + convertedC_BR_Address, labelCount);
          }
          strToPrint.add(instName + " label_" + labelMap.get(i + convertedC_BR_Address));
        } else {
          // Regular CB instruction
          instName = instructionMap.get(instruction.op_8);
          if(!labelMap.containsKey(i+convertedC_BR_Address)) {
            labelCount++;
            labelMap.put(i+convertedC_BR_Address, labelCount );
          }
          strToPrint.add(instName + " " + printRegister(instruction.rd)+ ", label_" + labelMap.get(i+convertedC_BR_Address ));
        }
      }

      else if (instructionMap.containsKey(instruction.op_10)) {
        // Handle I-type instructions
        String instructionString = instructionMap.get(instruction.op_10);
        strToPrint.add(instructionString + " " + printRegister(instruction.rd) + ", " + printRegister(instruction.rn) + ", #" + convertTo2s(instruction.aluImmediate, 12));
      }

      else if (instructionMap.containsKey(instruction.op_11)) {
        // Handle other instruction formats (e.g., D-type, R-type)
        String instructionString = instructionMap.get(instruction.op_11);
        switch (instruction.op_11) {
          case 0b11111000000, 0b11111000010 -> 
            strToPrint.add(instructionString + " " + printRegister(instruction.rd) + ", [" + printRegister(instruction.rn) + ", #" + instruction.DT_address + "]");
          case 0b11010011011, 0b11010011010 -> 
            strToPrint.add(instructionString + " " + printRegister(instruction.rd) + ", " + printRegister(instruction.rn) + ", #" + instruction.shamt);
          case 0b11010110000 -> 
            strToPrint.add(instructionString + " " + printRegister(instruction.rn));
          case 0b11111111100, 0b11111111110, 0b11111111111 -> 
            strToPrint.add(instructionString);
          case 0b11111111101, 0b11111111001 -> 
            strToPrint.add(instructionString + " " + printRegister(instruction.rd));
          default -> 
            strToPrint.add(instructionString + " " + printRegister(instruction.rd) + ", " + printRegister(instruction.rn) + ", " + printRegister(instruction.rm));
        }
      }

      else {
        // Invalid instruction encountered
        System.err.println("Invalid instruction! Instruction " + i + " is invalid!");
        return;
      }
    }

    // Sort and insert labels into the list of instructions
    List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(labelMap.entrySet());
    entryList.sort(Map.Entry.comparingByKey());

    int count = 0;
    for (Map.Entry<Integer, Integer> label : entryList) {
        if (label.getKey() + count >= strToPrint.size()) {
            strToPrint.add("label_" + label.getValue() + ": ");
        } else {
            strToPrint.add(label.getKey() + count, "label_" + label.getValue() + ": ");
        }
        count++;
    }

    // Print the instructions with labels
    for(String str : strToPrint) {
      System.out.println(str);
    }
  }

  // Converts a value to two's complement representation
  public static int convertTo2s(int num, int bits) {
    int mask = (1 << bits) - 1; 
    int maskedNum = num & mask;

    int signBit = 1 << (bits - 1);
    if ((maskedNum & signBit) != 0) {
        maskedNum -= (1 << bits);
    }

    return maskedNum;
  }

  // Prints a register in assembly syntax
  public static String printRegister(int regNum) {
    return switch (regNum) {
      case 28 -> "SP";
      case 29 -> "FP";
      case 30 -> "LR";
      case 31 -> "XZR";
      default -> "X" + regNum;
  };
  }
}
