// DO NOT EDIT
// Generated by JFlex 1.9.1 http://jflex.de/
// source: srcjflexcup/Lexer.flex

package main.output_jflex;

import java_cup.runtime.*;
import JFlex.sym;
import main.output_cup.Token;


@SuppressWarnings("fallthrough")
public class Lexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file. */
  public static final int YYEOF = -1;

  /** Initial size of the lookahead buffer. */
  private static final int ZZ_BUFFERSIZE = 16384;

  // Lexical states.
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 2;
  public static final int STRING = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1,  1,  2, 2
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\u10ff\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\1\0\1\1\1\3\22\0\1\1"+
    "\1\4\1\5\1\0\1\6\1\7\1\10\1\0\1\11"+
    "\1\12\1\13\1\14\1\15\1\16\1\17\1\20\12\21"+
    "\1\22\1\23\1\24\1\25\1\26\1\0\1\27\32\30"+
    "\1\0\1\31\1\0\1\32\1\30\1\0\1\33\1\34"+
    "\1\35\1\36\1\37\1\40\1\41\1\42\1\43\2\30"+
    "\1\44\1\30\1\45\1\46\1\47\1\30\1\50\1\51"+
    "\1\52\1\53\1\54\1\55\3\30\1\0\1\56\u0183\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[512];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\1\2\0\1\2\2\3\1\4\1\5\1\6\1\7"+
    "\1\2\1\10\1\11\1\12\1\13\1\14\1\15\1\16"+
    "\1\17\1\1\1\20\1\21\1\22\1\23\1\24\1\25"+
    "\1\26\1\27\1\2\14\26\1\2\1\30\1\31\1\32"+
    "\1\33\1\34\1\0\1\35\1\0\1\36\1\37\1\40"+
    "\1\41\1\26\1\42\4\26\1\43\11\26\1\44\1\45"+
    "\1\46\6\26\1\47\6\26\1\50\1\26\1\51\1\26"+
    "\1\52\5\26\1\53\1\26\1\54\1\55\2\26\1\56"+
    "\1\57\4\26\1\60\2\26\1\61\3\26\1\62\1\26"+
    "\1\63\4\26\1\64\1\65\1\66\1\67\1\70\1\26"+
    "\1\71\1\72";

  private static int [] zzUnpackAction() {
    int [] result = new int[130];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\57\0\136\0\215\0\215\0\274\0\215\0\215"+
    "\0\215\0\215\0\353\0\215\0\215\0\215\0\215\0\215"+
    "\0\u011a\0\u0149\0\215\0\u0178\0\215\0\215\0\u01a7\0\215"+
    "\0\u01d6\0\215\0\u0205\0\215\0\u0234\0\u0263\0\u0292\0\u02c1"+
    "\0\u02f0\0\u031f\0\u034e\0\u037d\0\u03ac\0\u03db\0\u040a\0\u0439"+
    "\0\u0468\0\u0497\0\215\0\215\0\u04c6\0\215\0\215\0\u04f5"+
    "\0\215\0\u0524\0\215\0\215\0\215\0\215\0\u0553\0\u0205"+
    "\0\u0582\0\u05b1\0\u05e0\0\u060f\0\u0205\0\u063e\0\u066d\0\u069c"+
    "\0\u06cb\0\u06fa\0\u0729\0\u0758\0\u0787\0\u07b6\0\215\0\u07e5"+
    "\0\215\0\u0814\0\u0843\0\u0872\0\u08a1\0\u08d0\0\u08ff\0\u0205"+
    "\0\u092e\0\u095d\0\u098c\0\u09bb\0\u09ea\0\u0a19\0\u0205\0\u0a48"+
    "\0\215\0\u0a77\0\u0aa6\0\u0ad5\0\u0b04\0\u0b33\0\u0b62\0\u0b91"+
    "\0\u0205\0\u0bc0\0\u0205\0\u0205\0\u0bef\0\u0c1e\0\u0205\0\u0205"+
    "\0\u0c4d\0\u0c7c\0\u0cab\0\u0cda\0\u0205\0\u0d09\0\u0d38\0\u0205"+
    "\0\u0d67\0\u0d96\0\u0dc5\0\u0205\0\u0df4\0\u0205\0\u0e23\0\u0e52"+
    "\0\u0e81\0\u0eb0\0\u0205\0\u0205\0\u0205\0\u0205\0\u0205\0\u0edf"+
    "\0\u0205\0\u0205";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[130];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\2\5\1\6\1\7\1\10\1\11\1\12\1\13"+
    "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
    "\1\34\1\35\1\33\1\36\1\33\1\37\1\40\1\41"+
    "\2\33\1\42\2\33\1\43\1\44\1\45\1\46\1\47"+
    "\1\33\1\50\1\51\1\52\7\53\1\54\47\53\2\55"+
    "\2\53\1\55\1\56\51\55\61\0\1\5\64\0\1\57"+
    "\64\0\1\60\7\0\1\61\51\0\1\22\54\0\1\22"+
    "\1\0\1\24\53\0\1\62\6\0\1\63\1\64\55\0"+
    "\1\65\52\0\1\33\6\0\1\33\2\0\23\33\26\0"+
    "\1\66\52\0\1\33\6\0\1\33\2\0\13\33\1\67"+
    "\7\33\22\0\1\33\6\0\1\33\2\0\13\33\1\70"+
    "\7\33\22\0\1\33\6\0\1\33\2\0\11\33\1\71"+
    "\1\72\10\33\22\0\1\33\6\0\1\33\2\0\1\73"+
    "\17\33\1\74\2\33\22\0\1\33\6\0\1\33\2\0"+
    "\5\33\1\75\4\33\1\76\10\33\22\0\1\33\6\0"+
    "\1\33\2\0\20\33\1\77\2\33\22\0\1\33\6\0"+
    "\1\33\2\0\15\33\1\100\5\33\22\0\1\33\6\0"+
    "\1\33\2\0\4\33\1\101\16\33\22\0\1\33\6\0"+
    "\1\33\2\0\17\33\1\102\3\33\22\0\1\33\6\0"+
    "\1\33\2\0\7\33\1\103\5\33\1\104\5\33\22\0"+
    "\1\33\6\0\1\33\2\0\1\105\22\33\22\0\1\33"+
    "\6\0\1\33\2\0\7\33\1\106\13\33\57\0\1\107"+
    "\2\55\2\0\1\55\1\0\51\55\26\0\1\110\46\0"+
    "\1\111\61\0\1\33\6\0\1\33\2\0\13\33\1\112"+
    "\7\33\22\0\1\33\6\0\1\33\2\0\16\33\1\113"+
    "\4\33\22\0\1\33\6\0\1\33\2\0\3\33\1\114"+
    "\17\33\22\0\1\33\6\0\1\33\2\0\11\33\1\115"+
    "\11\33\22\0\1\33\6\0\1\33\2\0\12\33\1\116"+
    "\10\33\22\0\1\33\6\0\1\33\2\0\17\33\1\117"+
    "\3\33\22\0\1\33\6\0\1\33\2\0\17\33\1\120"+
    "\3\33\22\0\1\33\6\0\1\33\2\0\13\33\1\121"+
    "\7\33\22\0\1\33\6\0\1\33\2\0\1\122\16\33"+
    "\1\123\3\33\22\0\1\33\6\0\1\33\2\0\15\33"+
    "\1\124\5\33\22\0\1\33\6\0\1\33\2\0\4\33"+
    "\1\125\16\33\22\0\1\33\6\0\1\33\2\0\20\33"+
    "\1\126\2\33\22\0\1\33\6\0\1\33\2\0\15\33"+
    "\1\127\5\33\22\0\1\33\6\0\1\33\2\0\10\33"+
    "\1\130\12\33\5\0\1\131\73\0\1\33\6\0\1\33"+
    "\2\0\11\33\1\132\11\33\22\0\1\33\6\0\1\33"+
    "\2\0\4\33\1\133\16\33\22\0\1\33\6\0\1\33"+
    "\2\0\5\33\1\134\2\33\1\135\3\33\1\136\5\33"+
    "\1\137\22\0\1\33\6\0\1\33\2\0\16\33\1\140"+
    "\4\33\22\0\1\33\6\0\1\33\2\0\2\33\1\141"+
    "\20\33\22\0\1\33\6\0\1\33\2\0\4\33\1\142"+
    "\16\33\22\0\1\33\6\0\1\33\2\0\2\33\1\143"+
    "\20\33\22\0\1\33\6\0\1\33\2\0\11\33\1\144"+
    "\11\33\22\0\1\33\6\0\1\33\2\0\20\33\1\145"+
    "\2\33\22\0\1\33\6\0\1\33\2\0\10\33\1\146"+
    "\12\33\22\0\1\33\6\0\1\33\2\0\12\33\1\147"+
    "\10\33\22\0\1\33\6\0\1\33\2\0\4\33\1\150"+
    "\16\33\22\0\1\33\6\0\1\33\2\0\11\33\1\151"+
    "\11\33\22\0\1\33\6\0\1\33\2\0\4\33\1\152"+
    "\16\33\22\0\1\33\6\0\1\33\2\0\10\33\1\153"+
    "\12\33\22\0\1\33\6\0\1\33\2\0\20\33\1\154"+
    "\2\33\22\0\1\33\6\0\1\33\2\0\5\33\1\155"+
    "\15\33\22\0\1\33\6\0\1\33\2\0\15\33\1\156"+
    "\5\33\22\0\1\33\6\0\1\33\2\0\7\33\1\157"+
    "\13\33\22\0\1\33\6\0\1\33\2\0\4\33\1\160"+
    "\16\33\22\0\1\33\6\0\1\33\2\0\6\33\1\161"+
    "\14\33\22\0\1\33\6\0\1\33\2\0\15\33\1\162"+
    "\5\33\22\0\1\33\6\0\1\33\2\0\12\33\1\163"+
    "\10\33\22\0\1\33\6\0\1\33\2\0\4\33\1\164"+
    "\16\33\22\0\1\33\6\0\1\33\2\0\1\165\22\33"+
    "\22\0\1\33\6\0\1\33\2\0\5\33\1\166\15\33"+
    "\22\0\1\33\6\0\1\33\2\0\12\33\1\167\10\33"+
    "\22\0\1\33\6\0\1\33\2\0\13\33\1\170\7\33"+
    "\22\0\1\33\6\0\1\33\2\0\10\33\1\171\12\33"+
    "\22\0\1\33\6\0\1\33\2\0\4\33\1\172\16\33"+
    "\22\0\1\33\6\0\1\33\2\0\12\33\1\173\10\33"+
    "\22\0\1\33\6\0\1\33\2\0\6\33\1\174\14\33"+
    "\22\0\1\33\6\0\1\33\2\0\12\33\1\175\10\33"+
    "\22\0\1\33\6\0\1\33\2\0\2\33\1\176\20\33"+
    "\22\0\1\33\6\0\1\33\2\0\2\33\1\177\20\33"+
    "\22\0\1\33\6\0\1\33\2\0\11\33\1\200\11\33"+
    "\22\0\1\33\6\0\1\33\2\0\15\33\1\201\5\33"+
    "\22\0\1\33\6\0\1\33\2\0\4\33\1\202\16\33"+
    "\1\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[3854];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** Error code for "Unknown internal scanner error". */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  /** Error code for "could not match input". */
  private static final int ZZ_NO_MATCH = 1;
  /** Error code for "pushback value was too large". */
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /**
   * Error messages for {@link #ZZ_UNKNOWN_ERROR}, {@link #ZZ_NO_MATCH}, and
   * {@link #ZZ_PUSHBACK_2BIG} respectively.
   */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\1\2\0\2\11\1\1\4\11\1\1\5\11\2\1"+
    "\1\11\1\1\2\11\1\1\1\11\1\1\1\11\1\1"+
    "\1\11\16\1\2\11\1\1\2\11\1\0\1\11\1\0"+
    "\4\11\20\1\1\11\1\1\1\11\17\1\1\11\51\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[130];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** Input device. */
  private java.io.Reader zzReader;

  /** Current state of the DFA. */
  private int zzState;

  /** Current lexical state. */
  private int zzLexicalState = YYINITIAL;

  /**
   * This buffer contains the current text to be matched and is the source of the {@link #yytext()}
   * string.
   */
  private char zzBuffer[] = new char[Math.min(ZZ_BUFFERSIZE, zzMaxBufferLen())];

  /** Text position at the last accepting state. */
  private int zzMarkedPos;

  /** Current text position in the buffer. */
  private int zzCurrentPos;

  /** Marks the beginning of the {@link #yytext()} string in the buffer. */
  private int zzStartRead;

  /** Marks the last character in the buffer, that has been read from input. */
  private int zzEndRead;

  /**
   * Whether the scanner is at the end of file.
   * @see #yyatEOF
   */
  private boolean zzAtEOF;

  /**
   * The number of occupied positions in {@link #zzBuffer} beyond {@link #zzEndRead}.
   *
   * <p>When a lead/high surrogate has been read from the input stream into the final
   * {@link #zzBuffer} position, this will have a value of 1; otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /** Number of newlines encountered up to the start of the matched text. */
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  private int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  private boolean zzEOFDone;

  /* user code: */
  private Symbol symbol (int type) {
    return new Symbol (type, yyline, yycolumn);
  }

  private Symbol symbol (int type, Object value) {
      return new Symbol (type, yyline, yycolumn, value);
  }

  public int getLine() { return yyline + 1; }

  public int getColumn() { return yycolumn + 1; }

  StringBuffer string_const = new StringBuffer();

  int s_line, s_col;


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  /**
   * Refills the input buffer.
   *
   * @return {@code false} iff there was new input.
   * @exception java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead - zzStartRead);

      /* translate stored positions */
      zzEndRead -= zzStartRead;
      zzCurrentPos -= zzStartRead;
      zzMarkedPos -= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate && zzCanGrow()) {
      /* if not, and it can grow: blow it up */
      char newBuffer[] = new char[Math.min(zzBuffer.length * 2, zzMaxBufferLen())];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      if (requested == 0) {
        throw new java.io.EOFException("Scan buffer limit reached ["+zzBuffer.length+"]");
      }
      else {
        throw new java.io.IOException(
            "Reader returned 0 characters. See JFlex examples/zero-reader for a workaround.");
      }
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
        if (numRead == requested) { // We requested too few chars to encode a full Unicode character
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        } else {                    // There is room in the buffer for at least one more char
          int c = zzReader.read();  // Expecting to read a paired low surrogate char
          if (c == -1) {
            return true;
          } else {
            zzBuffer[zzEndRead++] = (char)c;
          }
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }


  /**
   * Closes the input reader.
   *
   * @throws java.io.IOException if the reader could not be closed.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true; // indicate end of file
    zzEndRead = zzStartRead; // invalidate buffer

    if (zzReader != null) {
      zzReader.close();
    }
  }


  /**
   * Resets the scanner to read from a new input stream.
   *
   * <p>Does not close the old reader.
   *
   * <p>All internal variables are reset, the old input stream <b>cannot</b> be reused (internal
   * buffer is discarded and lost). Lexical state is set to {@code ZZ_INITIAL}.
   *
   * <p>Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader The new input stream.
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzEOFDone = false;
    yyResetPosition();
    zzLexicalState = YYINITIAL;
    int initBufferSize = Math.min(ZZ_BUFFERSIZE, zzMaxBufferLen());
    if (zzBuffer.length > initBufferSize) {
      zzBuffer = new char[initBufferSize];
    }
  }

  /**
   * Resets the input position.
   */
  private final void yyResetPosition() {
      zzAtBOL  = true;
      zzAtEOF  = false;
      zzCurrentPos = 0;
      zzMarkedPos = 0;
      zzStartRead = 0;
      zzEndRead = 0;
      zzFinalHighSurrogate = 0;
      yyline = 0;
      yycolumn = 0;
      yychar = 0L;
  }


  /**
   * Returns whether the scanner has reached the end of the reader it reads from.
   *
   * @return whether the scanner has reached EOF.
   */
  public final boolean yyatEOF() {
    return zzAtEOF;
  }


  /**
   * Returns the current lexical state.
   *
   * @return the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state.
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   *
   * @return the matched text.
   */
  public final String yytext() {
    return new String(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
  }


  /**
   * Returns the character at the given position from the matched text.
   *
   * <p>It is equivalent to {@code yytext().charAt(pos)}, but faster.
   *
   * @param position the position of the character to fetch. A value from 0 to {@code yylength()-1}.
   *
   * @return the character at {@code position}.
   */
  public final char yycharat(int position) {
    return zzBuffer[zzStartRead + position];
  }


  /**
   * How many characters were matched.
   *
   * @return the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * <p>In a well-formed scanner (no or only correct usage of {@code yypushback(int)} and a
   * match-all fallback rule) this method will only be called with things that
   * "Can't Possibly Happen".
   *
   * <p>If this method is called, something is seriously wrong (e.g. a JFlex bug producing a faulty
   * scanner etc.).
   *
   * <p>Usual syntax/scanner level error handling should be done in error fallback rules.
   *
   * @param errorCode the code of the error message to display.
   */
  private static void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    } catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * <p>They will be read again by then next call of the scanning method.
   *
   * @param number the number of characters to be read again. This number must not be greater than
   *     {@link #yylength()}.
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
    
  yyclose();    }
  }




  /**
   * Resumes scanning until the next regular expression is matched, the end of input is encountered
   * or an I/O-Error occurs.
   *
   * @return the next token.
   * @exception java.io.IOException if any I/O-Error occurs.
   */
  @Override  public java_cup.runtime.Symbol next_token() throws java.io.IOException
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char[] zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is
        // (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof)
            zzPeek = false;
          else
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
            switch (zzLexicalState) {
            case COMMENT: {
              throw new Error("Comment not close at line:" + s_line + ", col:" + s_col);
            }  // fall though
            case 131: break;
            case STRING: {
              throw new Error("String not close at line:" + s_line + ", col:" + s_col);
            }  // fall though
            case 132: break;
            default:
          { return new java_cup.runtime.Symbol(Token.EOF); }
        }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { return symbol(Token.INTEGER_CONST, yytext());
            }
          // fall through
          case 59: break;
          case 2:
            { throw new Error("Illegal character [" + yytext() + "] at line:" + getLine() + ", col:" + getColumn());
            }
          // fall through
          case 60: break;
          case 3:
            { /* ignore white spaces*/
            }
          // fall through
          case 61: break;
          case 4:
            { return symbol(Token.NOT);
            }
          // fall through
          case 62: break;
          case 5:
            { string_const = new StringBuffer();
          yybegin(STRING);
            }
          // fall through
          case 63: break;
          case 6:
            { return symbol(Token.DOLLARSIGN);
            }
          // fall through
          case 64: break;
          case 7:
            { s_line = getLine();
            s_col = getColumn();
            yybegin(COMMENT);
            }
          // fall through
          case 65: break;
          case 8:
            { return symbol(Token.LPAR);
            }
          // fall through
          case 66: break;
          case 9:
            { return symbol(Token.RPAR);
            }
          // fall through
          case 67: break;
          case 10:
            { return symbol(Token.TIMES);
            }
          // fall through
          case 68: break;
          case 11:
            { return symbol(Token.PLUS);
            }
          // fall through
          case 69: break;
          case 12:
            { return symbol(Token.COMMA);
            }
          // fall through
          case 70: break;
          case 13:
            { return symbol(Token.MINUS);
            }
          // fall through
          case 71: break;
          case 14:
            { return symbol(Token.REAL_CONST, yytext());
            }
          // fall through
          case 72: break;
          case 15:
            { return symbol(Token.DIV);
            }
          // fall through
          case 73: break;
          case 16:
            { return symbol(Token.COLON);
            }
          // fall through
          case 74: break;
          case 17:
            { return symbol(Token.SEMI);
            }
          // fall through
          case 75: break;
          case 18:
            { return symbol(Token.LT);
            }
          // fall through
          case 76: break;
          case 19:
            { return symbol(Token.EQ);
            }
          // fall through
          case 77: break;
          case 20:
            { return symbol(Token.GT);
            }
          // fall through
          case 78: break;
          case 21:
            { return symbol(Token.REF);
            }
          // fall through
          case 79: break;
          case 22:
            { return symbol(Token.ID, yytext());
            }
          // fall through
          case 80: break;
          case 23:
            { return symbol(Token.ENDVAR);
            }
          // fall through
          case 81: break;
          case 24:
            { /* Nothing */
            }
          // fall through
          case 82: break;
          case 25:
            { yybegin(YYINITIAL);
            }
          // fall through
          case 83: break;
          case 26:
            { string_const.append(yytext());
            }
          // fall through
          case 84: break;
          case 27:
            { yybegin(YYINITIAL);
        return symbol(Token.STRING_CONST, string_const.toString());
            }
          // fall through
          case 85: break;
          case 28:
            { return symbol(Token.AND);
            }
          // fall through
          case 86: break;
          case 29:
            { return symbol(Token.TYPERETURN);
            }
          // fall through
          case 87: break;
          case 30:
            { return symbol(Token.LE);
            }
          // fall through
          case 88: break;
          case 31:
            { return symbol(Token.NE);
            }
          // fall through
          case 89: break;
          case 32:
            { return symbol(Token.GE);
            }
          // fall through
          case 90: break;
          case 33:
            { return symbol(Token.ASSIGN);
            }
          // fall through
          case 91: break;
          case 34:
            { return symbol(Token.DO);
            }
          // fall through
          case 92: break;
          case 35:
            { return symbol(Token.IF);
            }
          // fall through
          case 93: break;
          case 36:
            { return symbol(Token.OR);
            }
          // fall through
          case 94: break;
          case 37:
            { return symbol(Token.WRITE);
            }
          // fall through
          case 95: break;
          case 38:
            { return symbol(Token.READ);
            }
          // fall through
          case 96: break;
          case 39:
            { return symbol(Token.OUT);
            }
          // fall through
          case 97: break;
          case 40:
            { return symbol(Token.VAR);
            }
          // fall through
          case 98: break;
          case 41:
            { return symbol(Token.WRITERETURN);
            }
          // fall through
          case 99: break;
          case 42:
            { return symbol(Token.ELSE);
            }
          // fall through
          case 100: break;
          case 43:
            { return symbol(Token.FUNCTION);
            }
          // fall through
          case 101: break;
          case 44:
            { return symbol(Token.PROCEDURE);
            }
          // fall through
          case 102: break;
          case 45:
            { return symbol(Token.REAL);
            }
          // fall through
          case 103: break;
          case 46:
            { return symbol(Token.THEN);
            }
          // fall through
          case 104: break;
          case 47:
            { return symbol(Token.TRUE);
            }
          // fall through
          case 105: break;
          case 48:
            { return symbol(Token.ENDIF);
            }
          // fall through
          case 106: break;
          case 49:
            { return symbol(Token.FALSE);
            }
          // fall through
          case 107: break;
          case 50:
            { return symbol(Token.WHILE);
            }
          // fall through
          case 108: break;
          case 51:
            { return symbol(Token.ELIF);
            }
          // fall through
          case 109: break;
          case 52:
            { return symbol(Token.RETURN);
            }
          // fall through
          case 110: break;
          case 53:
            { return symbol(Token.STRING);
            }
          // fall through
          case 111: break;
          case 54:
            { return symbol(Token.BOOLEAN);
            }
          // fall through
          case 112: break;
          case 55:
            { return symbol(Token.ENDFUNCTION);
            }
          // fall through
          case 113: break;
          case 56:
            { return symbol(Token.ENDPROCEDURE);
            }
          // fall through
          case 114: break;
          case 57:
            { return symbol(Token.INTEGER);
            }
          // fall through
          case 115: break;
          case 58:
            { return symbol(Token.ENDWHILE);
            }
          // fall through
          case 116: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
