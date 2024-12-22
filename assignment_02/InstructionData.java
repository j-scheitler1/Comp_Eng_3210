public class InstructionData {
  // Instruction fields decoded from a 32-bit binary instruction
  public int op_6;          // 6-bit operation code
  public int op_8;          // 8-bit operation code
  public int op_10;         // 10-bit operation code
  public int op_11;         // 11-bit operation code
  public int rn;            // Register number for the first operand
  public int rm;            // Register number for the second operand
  public int rd;            // Destination register number
  public int shamt;         // Shift amount for certain instructions
  public int aluImmediate;  // Immediate value for ALU operations
  public int DT_address;    // Data transfer address offset
  public int BR_address;    // Branch address offset for unconditional branches
  public int C_BR_address;  // Conditional branch address offset

  /**
   * Constructor to decode a 32-bit binary instruction.
   * Extracts various fields based on predefined bit lengths and positions.
   *
   * @param instruction A 32-bit binary instruction to decode.
   */
  public InstructionData(int instruction) {
    // Extracting the 6-bit operation code (bits 31-26)
    op_6 = instruction >> 26 & 0x3F;
    
    // Extracting the 8-bit operation code (bits 31-24)
    op_8 = instruction >> 24 & 0xFF;
    
    // Extracting the 10-bit operation code (bits 31-22)
    op_10 = instruction >> 22 & 0x3FF;
    
    // Extracting the 11-bit operation code (bits 31-21)
    op_11 = instruction >> 21 & 0x7FF;
    
    // Extracting the register number for operand 1 (bits 9-5)
    rn = instruction >> 5 & 0x1F;
    
    // Extracting the register number for operand 2 (bits 20-16)
    rm = instruction >> 16 & 0x1F;
    
    // Extracting the destination register number (bits 4-0)
    rd = instruction & 0x1F;
    
    // Extracting the shift amount (bits 15-10)
    shamt = instruction >> 10 & 0x3F;
    
    // Extracting the 12-bit immediate value for ALU operations (bits 21-10)
    aluImmediate = instruction >> 10 & 0xFFF;
    
    // Extracting the data transfer address offset (bits 20-12)
    DT_address = instruction >> 12 & 0x1FF;
    
    // Extracting the 26-bit branch address offset (bits 25-0)
    BR_address = instruction & 0x3FFFFFF;
    
    // Extracting the 19-bit conditional branch address offset (bits 23-5)
    C_BR_address = instruction >> 5 & 0x7FFFF;
  }
}
