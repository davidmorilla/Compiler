package com.grupo22.compiler.model;
import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;

import com.grupo22.compiler.util.EntryTS;
import com.grupo22.compiler.util.Token;
public class Compiler {
	static int contadovich=0;
	static BufferedWriter tokensW;
	static BufferedWriter parseW;
	static BufferedReader br;
	static FileWriter tokens;
	static FileWriter parses;
	static FileReader fileReader;

	/**
	 * @codigo tipo de token
	 * @atributo valor del token
	 */
	static Token token;
	static String parser ="Descendente ";
	static TSControl TSControl;



	static boolean hayError=false;
	static int hashFuncion=-1;
	static ArrayList<String> TiposParametros;
	static String funcionTratada = null;
	static int CuentaParametros=0;
	public final static String CODE_FILE_NAME_FORMAT = "src/main/java/com/grupo22/compiler/code/code%d.txt";
	public final static String TOKENS_OUTPUT_FORMAT = "src/main/java/com/grupo22/compiler/output/tokens_output%d.txt";
	public final static String PARSE_OUTPUT_FORMAT = "src/main/java/com/grupo22/compiler/output/parse_output%d.txt";

	final static int CODE_FILE_NUMBER = 8; //Cambiar aquí el numero de codigo de ejemplo a parsear

	public static void main (String args[]) {
		String CODE_FILE_NAME = String.format(CODE_FILE_NAME_FORMAT, CODE_FILE_NUMBER);
		String TOKENS_OUTPUT_FILE = String.format(TOKENS_OUTPUT_FORMAT, CODE_FILE_NUMBER);
		String PARSE_OUTPUT_FILE = String.format(PARSE_OUTPUT_FORMAT, CODE_FILE_NUMBER);
		try {
			parses = new FileWriter(PARSE_OUTPUT_FILE);
			tokens = new FileWriter(TOKENS_OUTPUT_FILE);
			tokensW = new BufferedWriter(tokens);
			parseW = new BufferedWriter(parses);
			fileReader = new FileReader(CODE_FILE_NAME);
			br = new BufferedReader(fileReader);
			char[] pointer ={ (char) br.read()};
			int[] line={1};
			A_sint(br,pointer,line, CODE_FILE_NUMBER);
			parseW.write(parser);
			TSControl.printTS();
			TSControl.closeWritingBuffer();
			tokensW.close();
			parseW.close();
			br.close();
		}catch (IOException e) {
			System.err.println("No se pudo crear el archivo");
		}
	}

	private static void A_sint(BufferedReader br, char[] pointer, int[] line, int CODE_FILE_NUMBER) {
		try {
			TSControl = new TSControl(CODE_FILE_NUMBER);
			token= A_lex(br,pointer, line,false);
			Entry<String[], Boolean> resSin= U(br,pointer,line, CODE_FILE_NUMBER);
			if(!resSin.getValue()){
				System.err.println("Corrija los errores existentes. Linea: "+ line[0] +"Mientras se trataba el token: "+ token.getCod()+"\nParser: "+parser);
			}else{
				System.out.println("Codigo valido hasta la linea:"+ line[0] + "\n" + parser);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static Token A_lex (BufferedReader br, char pointer[], int[] line, boolean hayDeclaracion) throws IOException{
		String caracter = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numero ="0123456789";
		while(pointer[0]!=65535) {
			//System.out.println(pointer);
			switch(pointer[0]) {
			case '{':
				pointer[0] = (char) br.read();
				return genToken("LLAVE",0);
			case '}':
				pointer[0] = (char) br.read();
				return genToken("LLAVE",1);
				//pointer = (char) br.read();
			case '(':
				pointer[0] = (char) br.read();
				return genToken("PARENT",0);
				//pointer = (char) br.read();
			case ')':
				pointer[0] = (char) br.read();
				return genToken("PARENT",1);
				//pointer = (char) br.read();
			case ';':
				pointer[0] = (char) br.read();
				return genToken("PYC","");
				//pointer = (char) br.read();
			case ',':
				pointer[0] = (char) br.read();
				return genToken("COMA","");
				//pointer = (char) br.read();
			case '"':
				String string="\"";
				pointer[0] = (char) br.read();
				int cont=0;
				boolean error=false;
				while(pointer[0]!='"' && !error) {
					if(pointer[0]=='\n' || pointer[0]=='\r' || pointer[0]==65535) {			
						genError(11,line[0], "\"");
						error=true; 
					} else if(pointer[0]=='\\'){
						string+=pointer[0];
						cont++;
						pointer[0]=(char)br.read();
						if(pointer[0]=='"' || pointer[0]=='\\'){
							string+=pointer[0];
							cont++;
							pointer[0]=(char)br.read();
						}
					} else {
						string+=pointer[0];
						cont++;
						pointer[0] = (char) br.read();
					}
				}
				if(cont>64)	{
					genError(16, line[0], "");
				}else if(!error){
					string+="\"";
					pointer[0]=(char)br.read();
					return genToken("CAD",string);
				}				
				break;
			case '=':
				pointer[0] = (char) br.read();
				if(pointer[0]=='=') {
					pointer[0] = (char) br.read();
					return genToken("EQ","");
				}
				else{
					return genToken("ASIG",0);
				}
			case '&':
				pointer[0] = (char) br.read();
				if(pointer[0]=='&') {
					pointer[0] = (char) br.read();
					return genToken("AND","");
				}
				else {
					genError(12,line[0],"&");    
				}  
				break;
			case '%':
				pointer[0] = (char) br.read();
				if(pointer[0]=='=') {
					pointer[0] = (char) br.read();
					return genToken("ASIG",1);
				}
				else
					return genToken("MOD","");         
			case '/':
				pointer[0] = (char) br.read();
				if(pointer[0]=='*') {
					pointer[0]=(char)br.read();
					boolean end=false;
					while(!end) {
						if(pointer[0]!=65535) {
							char pointer2=(char) br.read();
							if(pointer[0]=='*' && pointer2 == '/') {
								end=true;
								pointer[0]=(char) br.read();
							} else {
								pointer[0]=pointer2;
							}
						} else {
							genError(13,line[0],"*");
							end=true;
						}
					}
				} else {
					genError(14,line[0], "*");					
				}
				break;
			case '\r':
				pointer[0]=(char)br.read();
				break;
			case '\n':
				line[0]++;
				pointer[0]=(char)br.read();
				break;
			default:		//SI ES O.C, DIGITO O NUMERO
				if(caracter.contains(""+pointer[0])) {
					String identificador=""+pointer[0];
					pointer[0] = (char) br.read();
					while(caracter.contains(""+pointer[0]) || numero.contains(""+pointer[0])) {
						identificador+=pointer[0];
						pointer[0] = (char) br.read();
					}
					return genPR(identificador, line[0], hayDeclaracion);
				} 
				else if(numero.contains(""+pointer[0])) {
					short numeroaux=(short) Character.getNumericValue(pointer[0]);
					pointer[0] = (char) br.read();
					boolean legalnumber=true;
					while(numero.contains(""+pointer[0])) {
						numeroaux*=10;
						numeroaux+=Character.getNumericValue(pointer[0]);
						if(numeroaux<0 && legalnumber){
							genError(15,line[0], "");
							legalnumber=false;
						}
						pointer[0] = (char) br.read();
					}
					if(legalnumber){
						return genToken("CTE",numeroaux);
					}

				} else {//OTROS
					if(!(pointer[0]==' ' || pointer[0] == '\t')) {
						genError(10,line[0],""+ pointer[0]);
					}
					pointer[0] = (char) br.read();
				}
			}
		}
		tokensW.write("<FINAL,>\n");
		return new Token("FINAL", null);
	}

	private static Token genPR(String palabra, int line, boolean hayDeclaracion) {
		switch(palabra) {
		case "boolean": 
			return genToken("BOOL","");
		case"else": 
			return genToken("ELSE","");
		case "get":
			return genToken("GET","");
		case "if":
			return genToken("IF","");
		case "function":
			return genToken("FUNC","");
		case "int":
			return genToken("INT","");
		case "let":
			return genToken("LET","");
		case "put":
			return genToken("PUT","");
		case "return":
			return genToken("RET","");
		case "string":
			return genToken("STR","");
		case "void":
			return genToken("VOID","");
		case "false":
			return genToken("FALSE","");
		case "true": 
			return genToken("TRUE","");
		default: 
			if(TSControl.existeLex(palabra) ==-1 || (!TSControl.isGlobal() && TSControl.existeLex(palabra) == 0 )) { 
				//__hm__ts__.put(palabra,palabra.hashCode()); //modificado 3/01/23 21:23, anterior: __hm__ts__.put(palabra,contadovich++);
				TSControl.putSimbolo(palabra);
			}else if(hayDeclaracion){
				genError(17, line, palabra);
			}
			System.out.println(palabra + palabra.hashCode());
			return genToken("TABLEID",palabra.hashCode());	//TABLEID TIENE QUE SER UN NUMERO!!
		}
	}

	/* 	private static void imprime__hm__ts__(HashMap<String,Integer> __hm__ts__, int id) {
		try {
			tablaSW.write("#"+id+":\n");
			for(Entry<String, Integer> entry : __hm__ts__.entrySet()) {
				tablaSW.write("*'"+entry.getKey()+"'\n+despl:"+entry.getValue()+"\n");
			}
		} catch (IOException e) {
			return;
		}
	}
	 */
	private static Token genToken(String code,Object valor) {
		try {
			tokensW.write("<"+code+","+valor+">\n");
			return new Token(code,valor);
		} catch (IOException e) {
			return null;
		}
	}
	private static String tokenToString(Token token){
		switch(token.getCod()){
		case "BOOL":
			return "boolean";
		case "ELSE":
			return "else";
		case "GET":
			return "get";
		case "IF":
			return "if";
		case "FUNC":
			return "function";
		case "INT":
			return "int";
		case "LET":
			return "let";
		case "PUT":
			return "put";
		case "RET":
			return "return";
		case "STR":
			return "string";
		case "VOID":
			return "void";
		case "FALSE":
			return "false";
		case "TRUE":
			return "true";
		case "TABLEID":
			if(-1!=TSControl.existe((Integer)token.getAtr())){
				return TSControl.getVar((Integer)token.getAtr()).getNombreVar();
			}else{
				return "";
			}
		case "LLAVE":
			if(0==(Integer)token.getAtr()){
				return "{";
			}else{
				return "}";
			}
		case "PARENT":
			if(0==(Integer)token.getAtr()){
				return "(";
			}else{
				return ")";
			}
		case "PYC":
			return ";";
		case "COMA":
			return ",";
		case "CAD":
			return (String)token.getAtr();
		case "EQ":
			return "==";
		case "ASIG":
			if(0==(Integer)token.getAtr()){
				return "=";
			}else{
				return "%=";
			}
		case "AND":
			return "&&";
		case "MOD":
			return "%";
		case "CTE":
			return "" + (short)token.getAtr();
		case "FINAL":
			return "eof";
		default:
			return null;
		}
	}
	/**
	 * Genera por la salida de error un error de tipo lexico, sintactico o semantico.
	 * @param estado Tipo especÃ­fico de error 
	 * @param line Linea donde se encuentra el error
	 * @param error Caracter o simbolo que genera el error
	 */
	private static void genError(int estado, int line, String error) {	
		hayError=true;
		switch(estado){
		case 10:
			System.err.println("Error lexico en linea "+ line + ": char '" + error + "' no encontrado.");
			break;
		case 11:
			System.err.println("Error lexico en linea "+line +": falta '\"' de cierre de string.");
			break;
		case 12:
			System.err.println("Error lexico en linea "+line +": dos '&' deben ser usados para indicar 'AND'.");
			break;
		case 13:
			System.err.println("Error lexico en linea "+line +": comentario debe ser cerrado con '*/'.");
			break;
		case 14:
			System.err.println("Error lexico en linea "+line +": comentario debe empezar por  '/*' y no simplemente '/'.");
			break;
		case 15:
			System.err.println("Error lexico en linea "+line + ": constante de tipo entero fuera de rango(32767).");
			break;	
		case 16:
			System.err.println("Error lexico en linea "+line + ": longitud de string fuera de rango(64).");
			break;
		case 17:
			System.err.println("Error lexico en linea "+line + ": la variable '" + error + "' ya ha sido declarada anteriormente.");
			break;
		case 20: //inicio de sentencia
			System.err.println("Error sintactico en linea "+line + ": el simbolo '" + error + "' no puede utilizarse como inicio de sentencia.");
			break;	
		case 21: //debe venir en otro simbolo
			if(error.split("#")[0].equals("eof")){
				System.err.println("Error sintactico en linea "+line + ": el programa acaba sin haber cerrado todas las condiciones.");
			}else{
				System.err.println("Error sintactico en linea "+line + ": el simbolo '" + error.split("#")[0] + "' es incorrecto en esa posicion. Deberia haber '"+error.split("#")[1] + "'.");
			}
			break;
		case 30: 
			System.err.println("Error semantico en linea "+line + ": los tipos '" + error.split("#")[0] + "' y '" + error.split("#")[1] + "'' no coinciden." );
			break;
		case 31:
			System.err.println("Error semantico en linea "+line + ": el tipo de retorno '" + error.split("#")[1] +"' de la funcion '" + error.split("#")[0] + "' no coincide con el tipo devuelto '" + error.split("#")[2] + "'" );
			break;
		case 32:
			System.err.println("Error semantico en linea "+line + ": el tipo de entrada '" + error.split("#")[2] +"' de la posicion: " + error.split(error)[0] +"' de la funcion '" + error.split("#")[1] + "' no coincide con el tipo introducido '" + error.split("#")[3] + "'" );
			break;
		case 33: 
			System.err.println("Error semantico en linea "+line + ": el numero de parametros con el que se invoca a la funcion '" + error + "' es incorrecto." );
			break;
		case 34: 
			System.err.println("Error semantico en linea "+line + ": el numero de parametros ("+error+") con el que se crea la funcion es superior al maximo permitido (100)." );
			break;
		case 35: 
			System.err.println("Error semantico en linea "+line + ": la condición dentro de un if debe ser de tipo booleano y no de tipo " + error);
			break;
		case 36: 
			System.err.println("Error semantico en linea "+line + ": El identificador debe ser una funcion");
			break;
		case 37: //error por definir
			System.err.println("Error semantico en linea "+line + ": Error por definir");
			break;
		}
	}
	private static Entry<String[], Boolean> recuperacionError(BufferedReader br, char[] pointer, int[] line) throws Exception {
		while(!token.getCod().equals("PYC") && !token.getCod().equals("FINAL")){
			token=A_lex(br, pointer, line, false);
		}
		if(token.getCod().equals("PYC"))
			token=A_lex(br, pointer, line, false);
		return token.getCod().equals("FINAL")? new SimpleEntry<String[],Boolean>(devolverArray("null"),false): new SimpleEntry<String[],Boolean>(P(br, pointer, line).getKey(),false);
	}

	private static Entry<String[],Boolean> U(BufferedReader br, char[] pointer, int[] line, int CODE_FILE_NUMBER) throws Exception {
		parser+="1 ";
		if(P(br, pointer, line).getValue()) {
			return new SimpleEntry<String[], Boolean>(devolverArray("null"), true);
		} else {
			return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);
		}

	}

	private static Entry<String[],Boolean> P(BufferedReader br, char[] pointer, int[] line) throws Exception {

		if(token.getCod().equals("FUNC")){
			parser+="2 ";
			token=A_lex(br, pointer, line, true);
			TSControl.createTS("LOCAL");	//ASEM:  CreaTabla( )
			CuentaParametros = 0;			//ASEM:  CuentaParametrosDec:=0
			hashFuncion=-1;					//ASEM: hashcode de la funcion =-1 para reiniciar
			TiposParametros = new ArrayList<String>();  //ASEM:  TiposParametros[100]:=todoCeros
			Entry<String[], Boolean> resF=  F(br,pointer, line);
			if(resF.getValue()){				
				if( P(br, pointer, line).getValue()){
					return new SimpleEntry<String[], Boolean>(devolverArray("null"), true);
				}else{return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);}
			}else{return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);}
		}

		else if(token.getCod().equals("RET")||token.getCod().equals("GET")||token.getCod().equals("TABLEID")||token.getCod().equals("IF")||token.getCod().equals("LET")||token.getCod().equals("PUT")){
			parser+="3 ";
			if(B(br,pointer, line).getValue()){
				if( P(br, pointer, line).getValue()) {
					return new SimpleEntry<String[], Boolean>(devolverArray("null"), true);
				}else{return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);}
			}else{return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);}
		}else if(token.codigo.equals("FINAL")){//CASO LANDA
			//DestruirTabla();
			parser+="4 ";
			return new SimpleEntry<String[], Boolean>(devolverArray("null"), true);
		}else{
			genError(20, line[0], tokenToString(token));
			return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);
		}
	}

	private static Entry<String[],Boolean> S(BufferedReader br, char[] pointer, int[] line) throws Exception {

		if(token.getCod().equals("TABLEID")){			
			parser+="5 ";
			EntryTS temp = TSControl.getVar((int) token.atributo);
			if(temp!=null) {
				//TSControl.putSimboloEnGlobal(temp.getNombreVar(), "funcion");
			}
			token=A_lex(br, pointer, line, false);
			return Z(br,pointer,line);
		}
		else if(token.getCod().equals("PUT"))
		{
			parser+="6 ";
			token=A_lex(br, pointer, line, false);
			if(E(br,pointer,line).getValue()){

				if(token.getCod().equals("PYC")){
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}
				else{
					genError(21, line[0], tokenToString(token)+ "#;");
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}
		else if(token.getCod().equals("GET")){
			parser+="7 ";
			token=A_lex(br, pointer, line, false);
			if(token.getCod().equals("TABLEID")){
				token=A_lex(br, pointer, line, false);
				if(token.getCod().equals("PYC")){
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}
				else
				{
					genError(21, line[0], tokenToString(token)+ "#;");
					//token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			}
			else
			{
				genError(21, line[0], tokenToString(token)+ "#variable");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);

			}
		}
		else if(token.getCod().equals("RET"))
		{
			parser+="8 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resX = X(br,pointer,line);

			if(resX.getValue()){
				//Tipo de X no coincide con TipoRetorno
				/*ASEM: if(!resX.getKey()[0].equals(TSControl.getVar(funcionTratada[0].hashCode()).getTipoRetorno())) {
					genError(31, line[0], funcionTratada[0] +"#"+TSControl.getVar(funcionTratada[0].hashCode()).getTipoRetorno() +"#" +resX.getKey()[0] );
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
				}*/

				if(token.getCod().equals("PYC")){
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);					
				}else{
					genError(21, line[0], tokenToString(token)+ "#;");
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			}
			else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}
		else{
			genError(21, line[0], tokenToString(token) + "#variable' o 'put' o 'get' o 'return");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}	

	private static Entry<String[],Boolean> Z(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("ASIG") && (int)token.getAtr()==0){
			parser+="9 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resE=E(br,pointer,line);
			if(resE.getValue())
			{
				if(token.codigo.equals("PYC"))
				{
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(resE.getKey(),true);
				}else{
					genError(21, line[0], tokenToString(token)+ "#;");	
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}

			}
			else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}else if(token.codigo.equals("ASIG") && (int)token.getAtr()==1){
			parser+="10 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resE=E(br,pointer,line);
			if(resE.getValue())
			{
				if(token.codigo.equals("PYC"))
				{

					token=A_lex(br, pointer, line, false);
					/*if(resE.getKey()[0].equals("int")) {*/
					return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
					/*} else	//TIPO_ERROR
						genError(30, line[0], "int#" + resE.getKey()[0]);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
					 */
				}
				else{
					genError(21, line[0], tokenToString(token)+ "#;");
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
					/*
					//continua
					token=A_lex(br, pointer, line, false);
					if(resE.getKey()[0].equals("int")) {
						return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
					} else	//TIPO_ERROR
						genError(30, line[0], "int#" + resE.getKey()[0]);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);*/
				}
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}else if(token.getCod().equals("PARENT") && (int)token.getAtr()==0){
			parser+="11 ";
			token=A_lex(br, pointer, line, false);
			if(L(br,pointer,line).getValue()){
				if(token.getCod().equals("PARENT") && (int)token.getAtr()==1)
				{
					token=A_lex(br, pointer, line, false);
					if(token.codigo.equals("PYC")){	
						token=A_lex(br, pointer, line, false);
						return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
					}
					else{
						genError(21, line[0], tokenToString(token)+ "#;");
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
					}
				}
				else{
					genError(21, line[0], tokenToString(token)+ "#)");
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}
		else if(token.getCod().equals("PYC")){
			parser+="12 ";
			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token)+ "#;' o '(' o '=' o '%=");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> W(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("ASIG") && (int)token.getAtr()==0){
			parser+="13 ";
			token=A_lex(br, pointer, line, false);
			return E(br,pointer,line);
		}else if(token.codigo.equals("ASIG") && (int)token.getAtr()==1){
			parser+="14 ";
			token=A_lex(br, pointer, line, false);
			return E(br,pointer,line);

			/*if(resE[0].equals("int"))
				return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
			else {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}*/
		}else if(token.getCod().equals("PYC")){//CASO LAMBDA
			parser+="15 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token)+ "#;' o '=' o '%=");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			//return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> B(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("IF")){
			parser+="16 ";
			token=A_lex(br, pointer, line, false);
			if(token.getCod().equals("PARENT") && (int)token.getAtr()==0)
			{
				token=A_lex(br, pointer, line, false);
				//ASEM
				Entry<String[],Boolean> resE=E(br,pointer,line);
				if(resE.getValue())
				{
					//<ASEM>
					String tipoCondicion = resE.getKey()[0];
					if(!tipoCondicion.equals("boolean")){
						genError(35, line[0], tipoCondicion);
						throw new Exception("PRODUCCION B: La condición debe ser de tipo booleano");


					}
					//</ASEM>
					//ASEM: if(!resE.getKey().equals("boolean")){
					if(token.getCod().equals("PARENT") && (int)token.getAtr()==1){
						token=A_lex(br, pointer, line, false);

						if((token.getCod().equals("LLAVE") && (int) token.getAtr()==0) || token.getCod().equals("TABLEID") || token.getCod().equals("PUT") || token.getCod().equals("GET") ||token.getCod().equals("RET")) {
							return Y(br,pointer,line);
						}else {
							return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"), false);
						}
					}else{
						genError(21, line[0], tokenToString(token)+ "#)");
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"), false);
					}
					//ASEM: }else{
					//ASEM: 	genError(30, line[0], "boolean#" +resE.getKey()[0]);
					//ASEM: 	return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"), true);
					//ASEM: }
				}else{
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"), false);
				}
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"), false);
			}
		}else if(token.codigo.equals("LET")){
			parser+="17 ";
			token=A_lex(br, pointer, line, true);
			if(token.codigo.equals("TABLEID")){	
				//<ASEM>
				int lexem=(int)token.getAtr();
				//</ASEM>
				token=A_lex(br, pointer, line, false);
				System.out.println(token.toString());
				Entry <String[],Boolean> resT=T(br,pointer,line);
				if(resT.getValue())
				{
					Entry <String[],Boolean> resW =W(br,pointer,line);
					if(resW.getValue())
					{
						//<ASEM>
						String tipoDeclarado = resT.getKey()[0];
						String tipoAsignado = resW.getKey()[0];
						System.out.println("Atr:"+token.atributo.equals(""));
						if(tipoAsignado.equals("null")) { //no hay asignacion solo declaracion
							TSControl.putSimbolo(lexem, tipoDeclarado);
						}else if(tipoDeclarado.equals(tipoAsignado)) {
							TSControl.putSimbolo(lexem, tipoDeclarado);
						}else {
							genError(30, line[0], tipoDeclarado +"#"+ tipoAsignado);
							throw new Exception("PRODUCCION B: El tipo declarado no cuadra con el tipo asignado");
						}
						//</ASEM>

						if(token.getCod().equals("PYC")){
							token=A_lex(br, pointer, line, false);
							return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
						}else{
							genError(21, line[0], tokenToString(token)+ "#;");
							return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
						}
					}
					else{
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
					}
				}
				else{
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			}
			else{
				genError(21, line[0], tokenToString(token) + "#variable");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				/*
				//continua
				int lexem=(int)token.atributo;
				token=A_lex(br, pointer, line, false);
				String [] resT=T(br,pointer,line).getKey();
				if(!resT[0].equals("errorSin"))
				{
					String [] resW=W(br,pointer,line).getKey();
					if(!resW[0].equals("errorSin"))
					{
						if(!resT[0].equals(resW[0]) && !resW[0].equals("null")) {
							genError(30, line[0], resT[0]+ "#"+resW[0]);
							return new SimpleEntry<>(devolverArray("errorSem"), false);
						}else {//AniADETIPO
							TSControl.putSimbolo(TSControl.getVarName(lexem), resT[0]);
						}
						if(token.getCod().equals("PYC")){
							token=A_lex(br, pointer, line, false);
							return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
						}else{
							genError(21, line[0], tokenToString(token)+ "#;");
							//continua
							token=A_lex(br, pointer, line, false);
							return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
						}
					}
					else{
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
					}
				}
				else{
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}*/
			}
		}else {
			parser+="18 ";
			return S(br,pointer,line);
		}

	}

	private static Entry<String[],Boolean> Y(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("LLAVE") && ((int) token.atributo ==0)){
			parser+="19 ";
			token=A_lex(br, pointer, line, false);

			if(C(br,pointer,line).getValue())
			{
				if(token.getCod().equals("LLAVE") && (int)token.getAtr()==1)
				{
					token=A_lex(br, pointer, line, false);

					Entry<String[],Boolean> resYp=Yp(br,pointer,line);
					if(resYp.getValue()) {
						return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

					} else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
					/*
					if(token.getCod().equals("ELSE"))
					{
						token=A_lex(br, pointer, line, false);
						if(token.codigo.equals("LLAVE") && ((int) token.atributo ==0))
						{
							token=A_lex(br, pointer, line, false);
							if(C(br,pointer,line).getValue())
							{
								if(token.codigo.equals("LLAVE") && ((int) token.atributo ==1))
								{
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}else{
									genError(21, line[0],tokenToString(token) + "#}");
									return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{
							genError(21, line[0], tokenToString(token)+ "#{");
							return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
						}
					}else{
						genError(21, line[0], tokenToString(token)+ "#else");
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
					}
					 */
				}else{
					genError(21, line[0], tokenToString(token) + "#}");
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}






			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}else{
			if(token.getCod().equals("TABLEID") ||token.getCod().equals("PUT") ||token.getCod().equals("GET") ||token.getCod().equals("RET")){
				parser+="20 ";
				return S(br,pointer,line);
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
	}

	private static Entry<String[],Boolean> Yp(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("ELSE")) {
			parser+="56 ";
			token=A_lex(br, pointer, line, false);
			if(token.codigo.equals("LLAVE") && (int) token.atributo==0) {
				token=A_lex(br, pointer, line, false);
				Entry<String[],Boolean> resC= C(br,pointer,line);
				if(resC.getValue()) {
					if(token.codigo.equals("LLAVE") && (int) token.atributo==1) {
						token=A_lex(br, pointer, line, false);
						return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
					} else {
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
					}
				}else {
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			} else {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		} else if(token.codigo.equals("FUNC") || token.codigo.equals("IF") || token.codigo.equals("TABLEID") || token.codigo.equals("PUT") || token.codigo.equals("GET") || 
				token.codigo.equals("RET") || token.codigo.equals("LET") || (token.codigo.equals("LLAVE") && (int) token.atributo==1)) {
			parser+="57 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

		} else {
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}

	}	
	private static Entry<String[],Boolean> E(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if((token.codigo.equals("PARENT") && ((int) token.atributo ==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TABLEID")||token.codigo.equals("TRUE")){
			parser+="21 ";
			Entry<String[],Boolean> resG=G(br,pointer,line);
			//<ASEM>
			String tipoG = resG.getKey()[0];
			//</ASEM>
			if(resG.getValue()){
				Entry<String[],Boolean> resJ=J(br,pointer,line);
				if(resJ.getValue()){
					//<ASEM>
					String tipoJ = resJ.getKey()[0];
					System.out.println("TIPOOOOOS:"+tipoJ + tipoG);
					if((tipoJ.equals("null") && tipoG.equals("boolean") )|| (tipoJ.equals("boolean") && tipoJ.equals(tipoG))) {
						return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);
					}else {
						genError(30, line[0], tipoG + "#boolean");
						throw new Exception();
					}
					//</ASEM>
				}
				else{
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);

				}

			}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}

	}

	private static Entry<String[],Boolean> J(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("AND")){
			parser+="22 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resG=G(br,pointer,line);

			if(resG.getValue())
			{
				//<ASEM>
				String tipoG = resG.getKey()[0];
				if(tipoG.equals("boolean")) {
					Entry<String[],Boolean> resJ=J(br,pointer,line);
					String tipoJ = resJ.getKey()[0];
					if(tipoJ.equals(tipoG)) {
						return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);
					} else {
						genError(30, line[0], resG.getKey()[0] + "#boolean");
						throw new Exception();
					}
				}else {
					genError(30, line[0], resG.getKey()[0] + "#boolean");
					throw new Exception();
				}
				//</ASEM>

			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if((token.codigo.equals("PARENT") && ((int) token.atributo ==1))||token.codigo.equals("COMA")||token.codigo.equals("PYC")){
			parser+="23 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> G(BufferedReader br, char[] pointer, int[] line) throws Exception {
		parser+="24 ";
		Entry<String[],Boolean> resD=D(br,pointer,line);
		if(resD.getValue())
		{
			//<ASEM>
			String tipoD = resD.getKey()[0];
			Entry<String[],Boolean> resM=M(br,pointer,line,tipoD);
			//</ASEM>

			if(resM.getValue()){
				//<ASEM>
				String tipoM = resM.getKey()[0];
				if(tipoM.equals("boolean")) {
					return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);
				}else if(tipoM.equals("null")) {
					return new SimpleEntry<String[],Boolean>(devolverArray(tipoD),true);
				}else {
					genError(37,line[0], "");
					throw new Exception();
				}
				//</ASEM>
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}else{
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}


	private static Entry<String[],Boolean> M(BufferedReader br, char[] pointer, int[] line, String tipo) throws Exception {
		if(token.codigo.equals("EQ")){
			parser+="25 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resD=D(br,pointer,line);
			if(resD.getValue()){
				//<ASEM>
				String tipoD = resD.getKey()[0];
				if(tipoD.equals(tipo)) {
					Entry<String[],Boolean> resM=M(br,pointer,line, "boolean");
					String tipoM = resM.getKey()[0];
					return new SimpleEntry<String[],Boolean>(devolverArray(tipoM),true);
				}else {
					genError(30, line[0],tipoD+"#"+ tipo);
					throw new Exception();
				}
				//</ASEM>
			}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if((token.codigo.equals("PARENT") && ((int) token.atributo ==1))||token.codigo.equals("AND")||token.codigo.equals("COMA")||token.codigo.equals("PYC")){
			parser+="26 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}
	/*
	 deprecated

	 */
	private static Entry<String[],Boolean> D(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if((token.codigo.equals("PARENT") && ((int) token.atributo ==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TABLEID")||token.codigo.equals("TRUE")){
			parser+="27 ";
			Entry<String[],Boolean> resI=I(br,pointer,line);
			if(resI.getValue())
			{	
				String tipoI = resI.getKey()[0];
				Entry<String[],Boolean> resN=N(br,pointer,line);
				if(!resN.getValue()){
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
				else{
					//<ASEM>
					String tipoN = resN.getKey()[0];
					if(tipoN.equals("null") || (tipoI.equals("int") && tipoI.equals(tipoN))){
						return new SimpleEntry<String[],Boolean>(devolverArray(tipoI),true);
					}else {
						genError(30, line[0], resI.getKey()[0] +"#int");
						throw new Exception();
					}
					//</ASEM>	
				}
			} else {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}
		else{
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);

		}
	}

	private static Entry<String[],Boolean> N(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("MOD")){
			parser+="28 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resI=I(br,pointer,line);
			if(resI.getValue()){
				//<ASEM>
				String tipoI = resI.getKey()[0];
				if(tipoI.equals("int")) {
					Entry<String[],Boolean> resN=N(br,pointer,line);
					return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
				}else {
					genError(30, line[0], tipoI+"#int");
					throw new Exception();
				}
				//</ASEM>
			}
			else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}
		else if((token.codigo.equals("PARENT") && ((int) token.atributo ==1))||token.codigo.equals("AND")||token.codigo.equals("COMA")||token.codigo.equals("PYC")||token.codigo.equals("EQ")){
			parser+="29 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#%' o ')' o '&&' o ',' o ';' o '=='");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}

	private static Entry<String[],Boolean> I(BufferedReader br, char[] pointer, int[] line) throws Exception {
		String[] res=new String[1];
		if(token.codigo.equals("TABLEID")){
			parser+="30 ";
			//<ASEM>
			EntryTS id=TSControl.getVar((int)token.atributo);
			int lexema=(int) token.atributo;
			String tipoVar = id.getTipo();
			System.out.println("tipoVar: " + tipoVar);
			//</ASEM>
			//ASEM: funcionTratada[0]=idI.getNombreVar();	//GUARDAMOS NOMBRE FUNCION

			token=A_lex(br, pointer, line, false);

			//SI O FALLA, PETA
			Entry<String[],Boolean> resO=O(br,pointer,line, lexema);
			if(resO.getValue()) {
				//<ASEM>
				String tipoRetorno = resO.getKey()[0];
				if(tipoRetorno.equals("null")){ //no era funcion
					return new SimpleEntry<String[],Boolean>(devolverArray(tipoVar),true);
				}else if(tipoVar.equals("funcion")) { //si es funcion
					return new  SimpleEntry<String[],Boolean>(devolverArray(tipoRetorno),true);
				}else {
					genError(36, line[0], "");
					throw new Exception("El identificador no es una función");
				}
				//</ASEM>
				//ASEM: if((resO.getKey()[0].equals("null") && idI.getTipo().equals("funcion")) || (!idI.getTipo().equals("funcion") && resO.getKey()[0].equals("null"))) {	//!!!!!!! tipo.funcion no existe aun
				//1133//NO ENTIENDO BIEN ESTE ERROR
				//ASEM: 	return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),true);
				//ASEM: }

				//ASEM: res[0]=idI.getTipo();
				//ASEM: return new SimpleEntry<String[],Boolean>(res,true); 	
				//return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

			}else {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false); 				
			}

		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==0)){
			parser+="31 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resE=E(br,pointer,line);
			if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
				token=A_lex(br, pointer, line,false);
				res[0]=resE.getKey()[0];
				return new SimpleEntry<String[],Boolean>(res,true);	
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false); 				
				//ASEM: genError(21, line[0], tokenToString(token) + "#')");
				//continua
				//ASEM: token=A_lex(br, pointer, line,false);
				//ASEM: res[0]=resE.getKey()[0];
				//ASEM: return new SimpleEntry<String[],Boolean>(res,true);	
			}
		}
		else if(token.codigo.equals("CTE")){
			parser+="32 ";
			token=A_lex(br, pointer, line, false);
			res[0]="int";
			return new SimpleEntry<String[],Boolean>(res,true);
		}
		else if(token.codigo.equals("CAD")){
			parser+="33 ";
			token=A_lex(br, pointer, line,false);
			res[0]="string";
			return new SimpleEntry<String[],Boolean>(res,true);
		}
		else if(token.codigo.equals("TRUE")){
			parser+="34 ";
			token=A_lex(br, pointer, line, false);
			res[0]="boolean";
			return new SimpleEntry<String[],Boolean>(res,true);
		}
		else if(token.codigo.equals("FALSE")){
			parser+="35 ";
			token=A_lex(br, pointer, line, false);
			res[0]="boolean";
			return new SimpleEntry<String[],Boolean>(res,true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#(' o ')' o 'false' o 'true' o 'cadena' o 'constante' o 'variable");
			res[0]="errorSin";
			return new SimpleEntry<String[],Boolean>(res,false);
		}
	}

	private static Entry<String[],Boolean> O(BufferedReader br, char[] pointer, int[] line, int lexema) throws Exception {
		if(token.codigo.equals("PARENT") && ((int) token.atributo==0)){
			parser+="36 ";
			token=A_lex(br, pointer, line, false);
			if(L(br,pointer,line).getValue()){
				if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}
				else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}
			else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if(token.codigo.equals("MOD")||token.codigo.equals("AND")||(token.codigo.equals("PARENT") && ((int) token.atributo==1))||token.codigo.equals("COMA")||token.codigo.equals("PYC")||token.codigo.equals("EQ")){
			parser+="37 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}

	private static Entry<String[],Boolean> T(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("INT")){
			parser+="38 ";
			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
		}
		else if(token.codigo.equals("BOOL")){
			parser+="39 ";
			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);
		}
		else if(token.codigo.equals("STR")){
			parser+="40 ";
			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("string"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#int' o 'boolean' o 'string");
			//token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> L(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if((token.codigo.equals("PARENT") && ((int) token.atributo==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TRUE")||token.codigo.equals("TABLEID")){
			parser+="41 ";
			Entry<String[],Boolean> resE=E(br, pointer, line);
			/* ASEM: EntryTS id=TSControl.getVar(funcionTratada[0].hashCode());

			if(id.getTipoParamXX(CuentaParametros)!=null && id.getTipoParamXX(CuentaParametros).equals(resE.getKey()[0])) {
				CuentaParametros++;		
			} else{
				genError(32, line[0],CuentaParametros +"#" + funcionTratada[0]+"#"+ id.getTipoParamXX(CuentaParametros)+ "#" +resE.getKey()[0] );
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);}
			 */

			if(resE.getValue()){
				if(Q(br, pointer, line).getValue()){
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){//CASO LANDA
			parser+="42 ";
			//ASEM: EntryTS id=TSControl.getVar(funcionTratada[0].hashCode());
			//ASEM: if(id.getNumParam()>0) {
			//ASEM: 	genError(33, line[0], funcionTratada[0]);
			//ASEM: 	return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),true);			
			//ASEM: }
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#(' o ')' o 'false' o 'true' o 'cadena' o 'constante' o 'variable");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			//return recuperacionError(br, pointer, line);
		}
	}

	private static Entry<String[],Boolean> Q(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("COMA")){
			parser+="43 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resE = E(br, pointer, line);
			//ASEM: EntryTS id=TSControl.getVar(funcionTratada[0].hashCode());

			if(resE.getValue()){
				/*ASEM:   if(id.getTipoParamXX(CuentaParametros)!=null && id.getTipoParamXX(CuentaParametros).equals(resE.getKey()[0])) {
					CuentaParametros++;		
				} else 	{

					//NO ENTIENDO BIEN ESTE ERROR
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
				}*/
				if(Q(br, pointer, line).getValue()){
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
			parser+="44 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#,' o ')");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			//return recuperacionError(br, pointer, line);
		}
	}

	private static Entry<String[],Boolean> X(BufferedReader br, char[] pointer, int[] line)throws Exception {

		if((token.codigo.equals("PARENT") && ((int) token.atributo ==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TABLEID")||token.codigo.equals("TRUE")){
			parser+="45 ";
			return E(br, pointer, line);
		} else if (token.codigo.equals("PYC")){
			parser+="46 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("void"),true);
		}else {
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}


	}


	private static Entry<String[],Boolean> F(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("TABLEID")){
			parser+="47 ";
			//<ASEM>
			hashFuncion=(int)token.atributo;
			EntryTS ent= TSControl.getFromGlobal(hashFuncion);
			funcionTratada = ent.getNombreVar();//COGEMOS EL NOMBRE DE LA FUNCION
			TSControl.setNombreTabla(funcionTratada);
			TSControl.putSimboloEnGlobal(funcionTratada,"funcion");
			//</ASEM>

			token=A_lex(br, pointer, line, false);	
			Entry<String[],Boolean> resH=H(br,pointer,line);			
			if(resH.getValue()){

				//<ASEM>
				String[] tipoRetorno = resH.getKey();
				TSControl.setParametersFunc(funcionTratada,tipoRetorno[0],null,null,null,null);	
				//</ASEM>

				if(token.getCod().equals("PARENT") && (int)token.getAtr()==0){
					token=A_lex(br, pointer, line, false);

					if(A(br,pointer,line).getValue()){
						if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
							token=A_lex(br, pointer, line, false);
							if(token.codigo.equals("LLAVE") && ((int) token.atributo ==0)){
								token=A_lex(br, pointer, line, false);
								if(C(br,pointer,line).getValue()){
									if(token.codigo.equals("LLAVE") && ((int) token.atributo==1)){
										token=A_lex(br, pointer, line, false);
										TSControl.destroyTS();
										return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
									}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
								}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
					}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
				}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}

	private static Entry<String[],Boolean> H(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("VOID")){
			token=A_lex(br, pointer, line, false);
			parser+="49 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("void"),true);
		}else if(token.codigo.equals("INT") || token.codigo.equals("BOOL") || token.codigo.equals("STR")) {
			parser+="48 ";
			Entry<String[],Boolean> resT =T(br, pointer, line);
			if(resT.getValue()) {

				return resT;
			}
			else {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}else {
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}


	private static Entry<String[],Boolean> A(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("VOID")){
			parser+="51 ";

			//<ASEM>
			CuentaParametros = 0;
			TSControl.setParametersFunc(funcionTratada, null, CuentaParametros, null, null, null);
			//</ASEM>


			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

		}else if(token.codigo.equals("BOOL")||token.codigo.equals("INT")||token.codigo.equals("STR")){
			parser+="50 ";
			Entry<String[],Boolean> resT=T(br, pointer, line);
			if(!resT.getValue()){
				return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);
			}

			if(token.codigo.equals("TABLEID")){
				System.out.println("A ESTA EN GLOBAL:" + TSControl.isGlobal());
				//<ASEM>
				String tipoParam = resT.getKey()[0];
				CuentaParametros++;
				System.out.println("NPARAM:"+CuentaParametros);
				TiposParametros.add(tipoParam);
				TSControl.setParametersFunc(funcionTratada, null, CuentaParametros, TiposParametros, null, null);
				System.out.println("lexemaa;" + token.getAtr());
				TSControl.putSimbolo((int)token.getAtr(), tipoParam);
				//</ASEM>

				token=A_lex(br, pointer, line, false);

				if(K(br, pointer, line).getValue()) {
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}else {
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}


			}else{
				genError(21, line[0],tokenToString(token) + "#variable");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}else{
			genError(21, line[0],tokenToString(token) + "#variable");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}

	}

	private static Entry<String[],Boolean> K(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("COMA")){
			parser+="52 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resT=T(br, pointer, line);
			//GESTION ERROR
			//ASEM: TiposParametros.add(resT[0]);//{TiposParametros[CuentaParametrosDec]:=T.tipo;
			//ASEM: if(TiposParametros.size()>100) {
			//ASEM: 	genError(34, line[0], TiposParametros.size()+"");
			//ASEM: 	return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),true);
			//ASEM: }

			if(!resT.getValue()) {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}

			if(token.codigo.equals("TABLEID")){
				//<ASEM>
				String tipoParam = resT.getKey()[0];
				CuentaParametros++;
				TiposParametros.add(tipoParam);
				if(TiposParametros.size()>100) {
					genError(34, line[0], TiposParametros.size()+"");
					throw new Exception("PRODUCCION K: No puede haber mas de 100 parametros para una función");
				}

				TSControl.setParametersFunc(funcionTratada, null, CuentaParametros, TiposParametros, null, null);
				TSControl.putSimbolo((int)token.getAtr(), tipoParam);
				//</ASEM>

				token=A_lex(br, pointer, line, false);
				if(K(br, pointer, line).getValue()) {

					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				} else {
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
			}else{ 
				genError(21, line[0],tokenToString(token) + "#variable");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}	
		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){//CASO LANDA
			//NO  LLAMAMOS A TOKEN POR QUE NO COMPROBAMOS EQUIVALENCIA SINO QUE ES LA COMPROBACION DEL FOLLOW (CASO LANDA)
			parser+="53 ";
			//{TiposParametros[CuentaParametrosDec]:=T.tipo;
			//ASEM: String[] paramTipos= new String[TiposParametros.size()];
			//ASEM: for(int i=0;i<TiposParametros.size();i++){
			//ASEM: 	paramTipos[i]=TiposParametros.get(i);
			//ASEM: }
			//ASEM: TSControl.setParametersFunc(funcionTratada[0],null,TiposParametros.size(),paramTipos,funcionTratada[0],null);
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#,' o '(");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> C(BufferedReader br, char[] pointer, int[] line) throws Exception {
		if(token.codigo.equals("LLAVE") && ((int) token.atributo==1)){//CASO LANDA
			parser+="55 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else if(token.codigo.equals("GET")||token.codigo.equals("TABLEID")||token.codigo.equals("IF")||token.codigo.equals("LET")||token.codigo.equals("PUT")||token.codigo.equals("RET")){
			parser+="54 ";
			Entry<String[],Boolean> resB =B(br, pointer, line);

			if(!resB.getValue())return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);

			Entry<String[],Boolean> resC =C(br, pointer, line);
			if(resC.getValue()) {
				return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}	
		}else{
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}			

	//FORMATO: A\nB\nC
	private static String [] devolverArray(String arrayacrear) {
		return arrayacrear.split("\n");
	}
}