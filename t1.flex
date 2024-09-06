%%

%{
  private Trabalho1 yyparser;

  public Yylex(java.io.Reader r, Trabalho1 yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
LineTerminator = \r|\n|\r\n

%%

"$TRACE_ON"                  { yyparser.setDebug(true); }
"$TRACE_OFF"                 { yyparser.setDebug(false); }

"define"  	                 {return Trabalho1.DEFINE;}
"return" 	                   {return Trabalho1.RETURN;}
"print"                      {return Trabalho1.PRINT;}
"if"		                     { return Trabalho1.IF; }

[:jletter:][:jletterdigit:]* { return Trabalho1.IDENT; }  

[0-9]+ 	                     { return Trabalho1.NUMBER; }

";" |
"{" |
"}" |
"," |
"(" |
")"                       	  { return yytext().charAt(0); } 
"=="                          {return Trabalho1.EQUALS;}
"="                           {return Trabalho1.ATTRIBUTION;}
"<="                          {return Trabalho1.MENORIGUAL;}
"+"|
"-"|
"*"|
"/"                           {return Trabalho1.OP;}


{WHITE_SPACE_CHAR}+           { }
{LineTerminator}		          {}
. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
