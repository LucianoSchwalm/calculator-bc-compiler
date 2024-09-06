%%

%{
  private AsdrSample yyparser;

  public Yylex(java.io.Reader r, AsdrSample yyparser) {
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

"define"  	                 {return AsdrSample.DEFINE;}
"return" 	                   {return AsdrSample.RETURN;}
"print"                      {return AsdrSample.PRINT;}
"if"		                     { return AsdrSample.IF; }

[:jletter:][:jletterdigit:]* { return AsdrSample.IDENT; }  

[0-9]+ 	                     { return AsdrSample.NUMBER; }

";" |
"{" |
"}" |
"," |
"(" |
")"                       	  { return yytext().charAt(0); } 
"=="                          {return AsdrSample.EQUALS;}
"="                           {return AsdrSample.ATTRIBUTION;}
"<="                          {return AsdrSample.MENORIGUAL;}
"+"|
"-"|
"*"|
"/"                           {return AsdrSample.OP;}


{WHITE_SPACE_CHAR}+           { }
{LineTerminator}		          {}
. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
