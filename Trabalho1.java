import java.io.*;

public class Trabalho1 {

  private static final int BASE_TOKEN_NUM = 301;
  
  public static final int IDENT        = 301;
  public static final int NUMBER 	   = 302;
  public static final int ATTRIBUTION  = 303;
  public static final int IF	         = 304;
  public static final int MENORIGUAL	= 305;
  public static final int EQUALS       = 306;
  public static final int PRINT        = 307;
  public static final int DEFINE       = 308;
  public static final int RETURN       = 309;
  public static final int OP           = 310;
  
  
    public static final String tokenList[] = 
      {"IDENT",
		 "NUMBER", 
		 "ATTRIBUTION", 
		 "IF", 
		 "MENORIGUAL",
		 "EQUALS",
       "PRINT",
       "DEFINE",
       "RETURN",
       "OP"  };
                                      
  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  
  /* construtor da classe */
  public Trabalho1 (Reader r) {
      lexer = new Yylex (r, this);
  }

/*  GRAMÁTICA
 Prog -> listaFunc

ListaFunc -> FUNC ListaFunc 
	   | vazio

FUNC -> define ident ( PARAMS ) BLOCO

PARAMS -> ident ListaIdent
	| vazio
		
ListaIdent -> , ident ListaIdent
	    | vazio
			
BLOCO -> { CMD }

CMD -> 	  ATRIBUICAO CMD
	| if ( CONDICAO ) CMD
	| for ( ATRIBUICAO ; CONDICAO ; ident plusplus ) CMD
	| return EXP CMD
	| print PARAMETROPRINT CMD
	| BLOCO CMD
	| vazio
	
ATRIBUICAO -> indent attribution EXP
	
CONDICAO -> EXP RESTO1

RESTO1 -> menorigual EXP
	| equals EXP
	
EXP -> ( EXP op EXP )
	| VALUE
		
VALUE -> number
	|ident RESTO2
		
RESTO2 -> ( EXP )
	| vazio

PARAMETROPRINT -> EXP RESTO3 
		| string RESTO3

RESTO3 -> , PARAMETROPRINT
	| vazio
 */

  private void Prog() {
   
      if (laToken == '{') {
         if (debug) System.out.println("Prog --> Bloco");
         Bloco();
      }
      else 
        yyerror("esperado '{'");
   }

  private void Bloco() {
      if (debug) System.out.println("Bloco --> { Cmd }");
      //if (laToken == '{') {
         verifica('{');
         Cmd();
         verifica('}');
      //}
  }

  private void Cmd() {
      if (laToken == '{') {
         if (debug) System.out.println("Cmd --> Bloco");
         Bloco();
	   }    
      else if (laToken == WHILE) {
         if (debug) System.out.println("Cmd --> WHILE ( E ) Cmd");
         verifica(WHILE);    // laToken = this.yylex(); 
  		   verifica('(');
  		   E();
         verifica(')');
         Cmd();
	   }
      else if (laToken == IDENT ) {
         if (debug) System.out.println("Cmd --> IDENT = E ;");
            verifica(IDENT);  
            verifica('='); 
            E();
		      verifica(';');
	   }
    else if (laToken == IF) {
         if (debug) System.out.println("Cmd --> if (E) Cmd RestoIF");
         verifica(IF);
         verifica('(');
  		   E();
         verifica(')');
         Cmd();
         RestoIF();
	   }
 	else yyerror("Esperado {, if, while ou identificador");
   }


   private void RestoIF() {
       if (laToken == ELSE) {
         if (debug) System.out.println("RestoIF --> else Cmd FI ");
         verifica(ELSE);
         Cmd();
         verifica(FI);
    
	   } else if (laToken == FI){
         if (debug) System.out.println("RestoIF -->  FI  ");
         verifica(FI); 
         }
      else yyerror("Esperado else ou fi");
     }     



  private void E() {
      if (laToken == IDENT) {
         if (debug) System.out.println("E --> IDENT");
         verifica(IDENT);
	   }
      else if (laToken == NUM) {
         if (debug) System.out.println("E --> NUM");
         verifica(NUM);
	   }
      else if (laToken == '(') {
         if (debug) System.out.println("E --> ( E )");
         verifica('(');
         E();        
		 verifica(')');
	   }
 	else yyerror("Esperado operando (, identificador ou numero");
   }


  private void verifica(int expected) {
      if (laToken == expected)
         laToken = this.yylex();
      else {
         String expStr, laStr;       

		expStr = ((expected < BASE_TOKEN_NUM )
                ? ""+(char)expected
			     : tokenList[expected-BASE_TOKEN_NUM]);
         
		laStr = ((laToken < BASE_TOKEN_NUM )
                ? Character.toString(laToken)
                : tokenList[laToken-BASE_TOKEN_NUM]);

          yyerror( "esperado token: " + expStr +
                   " na entrada: " + laStr);
     }
   }

   /* metodo de acesso ao Scanner gerado pelo JFLEX */
   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0); //zera o valor do token
           retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
          }
       return retVal; //retorna o token para o Parser 
   }

  /* metodo de manipulacao de erros de sintaxe */
  public void yyerror (String error) {
     System.err.println("Erro: " + error);
     System.err.println("Entrada rejeitada");
     System.out.println("\n\nFalhou!!!");
     System.exit(1);
     
  }

  public void setDebug(boolean trace) {
      debug = true;
  }


  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param args   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String[] args) {
     Trabalho1 parser = null;
     try {
         if (args.length == 0)
            parser = new Trabalho1(new InputStreamReader(System.in));
         else 
            parser = new  Trabalho1( new java.io.FileReader(args[0]));

          parser.setDebug(false);
          laToken = parser.yylex();          

          parser.Prog();
     
          if (laToken== Yylex.YYEOF)
             System.out.println("\n\nSucesso!");
          else     
             System.out.println("\n\nFalhou - esperado EOF.");               

        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+args[0]+"\"");
        }
//        catch (java.io.IOException e) {
//          System.out.println("IO error scanning file \""+args[0]+"\"");
//          System.out.println(e);
//        }
//        catch (Exception e) {
//          System.out.println("Unexpected exception:");
//          e.printStackTrace();
//      }
    
  }
  
}

