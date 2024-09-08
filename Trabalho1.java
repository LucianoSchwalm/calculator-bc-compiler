import java.io.*;
//Nomes: Enzo Martins e Luciano Schwalm
//Matriculas: 21200756 e 20106983
//Emails: enzo.martins@edu.pucrs.br e luciano.schwalm@edu.pucrs.br
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
  public static final int FOR	         = 311;
  public static final int PLUSPLUS	   = 312;
  
  
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
       "OP",  
       "FOR",
       "PLUSPLUS"
      };
                                      
  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  
  /* construtor da classe */
  public Trabalho1 (Reader r) {
      lexer = new Yylex (r, this);
  }

/*  GRAMATICA
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
	
CONDICAO -> EXP RESTOCONDICAO

RESTOCONDICAO -> menorigual EXP
	| equals EXP
	
EXP -> ( EXP op EXP )
	| VALUE
		
VALUE -> number
	|ident RESTOVALUE
		
RESTOVALUE -> ( EXP )
	| vazio

PARAMETROPRINT -> EXP RESTOPARAMETROPRINT

RESTOPARAMETROPRINT -> , PARAMETROPRINT
	| vazio
 */

   private void Prog() {
      ListaFunc();
   }

   private void ListaFunc(){
      if(laToken == DEFINE){
         System.out.println();
         Func();
         ListaFunc();
      }
   }

   private void Func(){
      verifica(DEFINE);
      verifica(IDENT);
      verifica('(');
      Params();
      verifica(')');
      Bloco();
   }

   private void Params(){
      if(laToken == IDENT){
         verifica(IDENT);
         ListaIdent();
      }
   }

   private void ListaIdent(){
      if(laToken == ','){
         verifica(',');
         verifica(IDENT);
         ListaIdent();
      }
   }

   private void Bloco(){
      verifica('{');
      Cmd();
      verifica('}');
   }

   private void Cmd(){
      if(laToken == IDENT){
         Atribuicao();
         Cmd();
      }
      else if(laToken == IF){
         verifica(IF);
         verifica('(');
         Condicao();
         verifica(')');
         Cmd();
      }
      else if(laToken == FOR){
         verifica(FOR);
         verifica('(');
         Atribuicao();
         verifica(';');
         Condicao();
         verifica(';');
         verifica(IDENT);
         verifica(PLUSPLUS);
         verifica(')');
         Cmd();
      }
      else if(laToken == RETURN){
         verifica(RETURN);
         Exp();
         Cmd();
      }
      else if(laToken == PRINT){
         verifica(PRINT);
         ParametroPrint();
         Cmd();
      }
      else if(laToken == '{'){
         Bloco();
         Cmd();
      }
   }

   private void Atribuicao(){
      verifica(IDENT);
      verifica(ATTRIBUTION);
      Exp();
   }

   private void Condicao(){
      Exp();
      RestoCondicao();
   }

   private void RestoCondicao(){
      if(laToken == MENORIGUAL) {
         verifica(MENORIGUAL);
         Exp();
      }
      else if(laToken == EQUALS){
         verifica(EQUALS);
         Exp();
      }
      else yyerror("Esperado <= ou ==");
   }

   private void Exp(){
      if(laToken == '('){
         verifica('(');
         Exp();
         verifica(OP);
         Exp();
         verifica(')');
      }
      else if (laToken == NUMBER || laToken == IDENT){
         Value();
      }
      else yyerror("Esperado (, Numero ou Identificador");
   }

   private void Value(){
      if(laToken == NUMBER){
         verifica(NUMBER);
      }
      else if (laToken == IDENT){
         verifica(IDENT);
         RestoValue();
      }
      else yyerror("Esperado Numero ou Identificador");
   }

   private void RestoValue(){
      if(laToken == '(') {
         verifica('(');
         Exp();
         verifica(')');
      }
   }

   private void ParametroPrint(){
      Exp();
      RestoParametroPrint();
   }

   private void RestoParametroPrint(){
      if(laToken == ',') {
         verifica(',');
         ParametroPrint();
      }
   }


  private void verifica(int expected) {
     String expStr, laStr;       
      if (laToken == expected){
         expStr = ((expected < BASE_TOKEN_NUM )
                ? ""+(char)expected
			     : tokenList[expected-BASE_TOKEN_NUM]);
         System.out.print(expStr + " ");
         laToken = this.yylex();
      }
      else {

         expStr = ((expected < BASE_TOKEN_NUM )
                  ? ""+(char)expected
               : tokenList[expected-BASE_TOKEN_NUM]);
            
         laStr = ((laToken < BASE_TOKEN_NUM )
                  ? Character.toString(laToken)
                  : tokenList[laToken-BASE_TOKEN_NUM]);

            yyerror( "esperava token: " + expStr +
                     " e recebeu: " + laStr);
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

