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
	static BufferedReader br;
	static FileWriter tokens;
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
	static String[] funcionTratada = new String[1];
	static int CuentaParametros=0;
	public final static String CODE_FILE_NAME_FORMAT = "src/main/java/com/grupo22/compiler/code/code%d.txt";
	public final static String TOKENS_OUTPUT_FORMAT = "src/main/java/com/grupo22/compiler/output/tokens_output%d.txt";
	public static void main (String args[]) {
		int CODE_FILE_NUMBER = 1; //Cambiar aquí el numero de codigo de ejemplo a parsear
		String CODE_FILE_NAME = String.format(CODE_FILE_NAME_FORMAT, CODE_FILE_NUMBER);
		String TOKENS_OUTPUT_FILE = String.format(TOKENS_OUTPUT_FORMAT, CODE_FILE_NUMBER);
		FileWriter tokens;
		FileReader fileReader;
		try {

			tokens = new FileWriter(TOKENS_OUTPUT_FILE);
			tokensW = new BufferedWriter(tokens);
			fileReader = new FileReader(CODE_FILE_NAME);
			br = new BufferedReader(fileReader);
			char[] pointer ={ (char) br.read()};
			int[] line={1};
			A_sint(br,pointer,line, CODE_FILE_NUMBER);
		}catch (IOException e) {
			System.err.println("Couldn't create a new file");
		}
		TSControl.printTS();//imprime__hm__ts__(__hm__ts__,100); //comentado el 3/01/23 21:26
		try {
			TSControl.closeWritingBuffer();
			tokensW.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void A_sint(BufferedReader br, char[] pointer, int[] line, int CODE_FILE_NUMBER) {
		try {
			token= A_lex(br,pointer, line,false);
			U(br,pointer,line, CODE_FILE_NUMBER).getValue();
			if(hayError){
				System.err.println("Corrija los errores existentes.");
			}else{
				System.out.println("Codigo valido hasta la linea:"+ line[0] + "\n" + parser);
			}

		} catch (IOException e) {
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
		default: //probablemente aqui unicamente generara siempre el token y se comprobara su ya existencia y aÃƒÂ±adira a la ts mÃƒÂ¡s adelante en el a.semantico //por hacer
			if(TSControl.existeLex(palabra) ==-1) { 
				//__hm__ts__.put(palabra,palabra.hashCode()); //modificado 3/01/23 21:23, anterior: __hm__ts__.put(palabra,contadovich++);
				TSControl.putSimbolo(palabra); 
			}else if(hayDeclaracion){
				genError(17, line, palabra);
			}
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
		}
	}
	private static Entry<String[], Boolean> recuperacionError(BufferedReader br, char[] pointer, int[] line) throws IOException{
		while(!token.getCod().equals("PYC") && !token.getCod().equals("FINAL")){
			token=A_lex(br, pointer, line, false);
		}
		if(token.getCod().equals("PYC"))
			token=A_lex(br, pointer, line, false);
		return token.getCod().equals("FINAL")? new SimpleEntry<String[],Boolean>(devolverArray("null"),false): new SimpleEntry<String[],Boolean>(P(br, pointer, line).getKey(),false);
	}

	private static Entry<String[],Boolean> U(BufferedReader br, char[] pointer, int[] line, int CODE_FILE_NUMBER) throws IOException {
		parser+="1 ";
		//ASEM: INICIALIZA LA TABLA DE SIMBOLOS CON EL NOMBRE GLOBAL
		TSControl = new TSControl(CODE_FILE_NUMBER);
		return P(br, pointer, line);
	}

	private static Entry<String[],Boolean> P(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.getCod().equals("FUNC")){
			parser+="2 ";
			token=A_lex(br, pointer, line, true);
			TSControl.createTS("LOCAL");

			if(F(br,pointer, line).getValue()){
				return P(br, pointer, line);
			}else{
				return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);}

		}else if(token.getCod().equals("RET")||token.getCod().equals("GET")||token.getCod().equals("TABLEID")||token.getCod().equals("IF")||token.getCod().equals("LET")||token.getCod().equals("PUT")){
			parser+="3 ";
			if(B(br,pointer, line).getValue()){
				return P(br, pointer, line);
			}else{return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);}
		}else if(token.codigo.equals("FINAL")){

			//DestruirTabla();
			parser+="4 ";
			return new SimpleEntry<String[], Boolean>(devolverArray("null"), true);
		}else{
			genError(20, line[0], tokenToString(token));
			return recuperacionError(br, pointer, line);
		}
	}

	private static Entry<String[],Boolean> S(BufferedReader br, char[] pointer, int[] line) throws IOException {

		if(token.getCod().equals("TABLEID")){
			//Quien lo haya hecho porfa que me explique cual es el objetivo.
			//Escrito por David.

			EntryTS temp = TSControl.getVar((int) token.atributo);

			if(temp!=null) {
				TSControl.putSimboloEnGlobal(temp.getNombreVar(), "funcion");
			}

			parser+="5 ";
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
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
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
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

				}
			}
			else
			{
				genError(21, line[0], tokenToString(token)+ "#variable");
				//continua
				token=A_lex(br, pointer, line, false);
				if(token.getCod().equals("PYC")){
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}
				else
				{
					genError(21, line[0], tokenToString(token)+ "#;");
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

				}
			}
		}
		else if(token.getCod().equals("RET"))
		{
			parser+="8 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resX = X(br,pointer,line);

			if(resX.getValue()){
				//Tipo de X no coincide con TipoRetorno
				if(!resX.getKey()[0].equals(TSControl.getVar(funcionTratada[0].hashCode()).getTipoRetorno())) {
					genError(31, line[0], funcionTratada[0] +"#"+TSControl.getVar(funcionTratada[0].hashCode()).getTipoRetorno() +"#" +resX.getKey()[0] );
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
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
			genError(21, line[0], tokenToString(token) + "#variable' o 'put' o 'get' o 'return");
			return recuperacionError(br, pointer, line);
		}
	}	

	private static Entry<String[],Boolean> Z(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("ASIG") && (int)token.getAtr()==0){
			parser+="9 ";
			token=A_lex(br, pointer, line, false);
			System.out.println(line[0]);
			Entry<String[],Boolean> resE=E(br,pointer,line);
			System.out.println("565 "+resE.getValue());
			if(resE.getValue())
			{
				System.out.println(line[0]);
				if(token.codigo.equals("PYC"))
				{
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(resE.getKey(),true);
				}
				else{
					System.out.println("572: " + line);
					genError(21, line[0], tokenToString(token)+ "#;");
					// continua
					token=A_lex(br, pointer, line, false);
					return new SimpleEntry<String[],Boolean>(resE.getKey(),true);
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
					if(resE.getKey()[0].equals("int")) {
						return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
					} else	//TIPO_ERROR
						genError(30, line[0], "int#" + resE.getKey()[0]);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
				}
				else{
					genError(21, line[0], tokenToString(token)+ "#;");
					//continua
					token=A_lex(br, pointer, line, false);
					if(resE.getKey()[0].equals("int")) {
						return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
					} else	//TIPO_ERROR
						genError(30, line[0], "int#" + resE.getKey()[0]);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
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
						//continua
						token=A_lex(br, pointer, line, false);
						return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

					}
				}
				else{
					genError(21, line[0], tokenToString(token)+ "#)");
					//continua
					token=A_lex(br, pointer, line, false);
					if(token.codigo.equals("PYC")){	
						token=A_lex(br, pointer, line, false);
						return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
					}
					else{
						genError(21, line[0], tokenToString(token)+ "#;");
						//continua
						token=A_lex(br, pointer, line, false);
						return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

					}
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
			return recuperacionError(br, pointer, line);
		}
	}

	private static Entry<String[],Boolean> W(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("ASIG") && (int)token.getAtr()==0){
			parser+="13 ";
			token=A_lex(br, pointer, line, false);
			return E(br,pointer,line);
		}else if(token.codigo.equals("ASIG") && (int)token.getAtr()==1){
			parser+="14 ";
			token=A_lex(br, pointer, line, false);
			String [] resE=E(br,pointer,line).getKey();
			if(resE[0].equals("int"))
				return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);
			else {
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}

		}else if(token.getCod().equals("PYC")){
			parser+="15 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token)+ "#;' o '=' o '%=");
			return recuperacionError(br, pointer, line);
			//return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> B(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("IF")){
			parser+="16 ";
			token=A_lex(br, pointer, line, false);
			if(token.getCod().equals("PARENT") && (int)token.getAtr()==0)
			{
				token=A_lex(br, pointer, line, false);

				//ASEM
				Entry<String[],Boolean> resE=E(br,pointer,line);
				if(!resE.getKey().equals("boolean")){
					if(token.getCod().equals("PARENT") && (int)token.getAtr()==1){
						return Y(br,pointer,line);
					}else{
						genError(21, line[0], tokenToString(token)+ "#)");
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"), false);
					}
				}else{
					genError(30, line[0], "boolean#" +resE.getKey()[0]);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"), true);
				}
			}else{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"), false);
			}
		}else if(token.codigo.equals("LET")){
			parser+="17 ";
			token=A_lex(br, pointer, line, true);
			if(token.codigo.equals("TABLEID")){	
				int lexem=(int)token.atributo;
				token=A_lex(br, pointer, line, false);
				String [] resT=T(br,pointer,line).getKey();
				if(!resT[0].equals("errorSin"))
				{
					String [] resW=W(br,pointer,line).getKey();

					if(!resW[0].equals("errorSin"))
					{
						if(!resT[0].equals(resW[0]) && !resW[0].equals("null") ) {
							genError(30, line[0], resT[0]+"#"+resW[0]);
							return new SimpleEntry<>(devolverArray("errorSem"), false);
						}else {//Aniade tipo
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
				}
			}
			else{
				genError(21, line[0], tokenToString(token) + "#variable");
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
				}
			}
		}else{
			parser+="18 ";
			return S(br,pointer,line);
		}
	}

	private static Entry<String[],Boolean> Y(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("LLAVE") && ((int) token.atributo ==0)){
			parser+="19 ";
			token=A_lex(br, pointer, line, false);
			if(C(br,pointer,line).getValue())
			{
				if(token.getCod().equals("LLAVE") && (int)token.getAtr()==1)
				{
					token=A_lex(br, pointer, line, false);
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
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{
							genError(21, line[0], tokenToString(token)+ "#{");
							//continua
							token=A_lex(br, pointer, line, false);
							if(C(br,pointer,line).getValue())
							{
								if(token.codigo.equals("LLAVE") && ((int) token.atributo ==1))
								{
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}else{
									genError(21, line[0],tokenToString(token) + "#}");
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}
					}else{
						genError(21, line[0], tokenToString(token)+ "#else");
						//continua
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
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{
							genError(21, line[0], tokenToString(token)+ "#{");
							//continua
							token=A_lex(br, pointer, line, false);
							if(C(br,pointer,line).getValue())
							{
								if(token.codigo.equals("LLAVE") && ((int) token.atributo ==1))
								{
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}else{
									genError(21, line[0],tokenToString(token) + "#}");
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}
					}
				}else{
					genError(21, line[0], tokenToString(token) + "#}");
					//continua
					token=A_lex(br, pointer, line, false);
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
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{
							genError(21, line[0], tokenToString(token)+ "#{");
							//continua
							token=A_lex(br, pointer, line, false);
							if(C(br,pointer,line).getValue())
							{
								if(token.codigo.equals("LLAVE") && ((int) token.atributo ==1))
								{
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}else{
									genError(21, line[0],tokenToString(token) + "#}");
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}
					}else{
						genError(21, line[0], tokenToString(token)+ "#else");
						//continua
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
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{
							genError(21, line[0], tokenToString(token)+ "#{");
							//continua
							token=A_lex(br, pointer, line, false);
							if(C(br,pointer,line).getValue())
							{
								if(token.codigo.equals("LLAVE") && ((int) token.atributo ==1))
								{
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}else{
									genError(21, line[0],tokenToString(token) + "#}");
									//continua
									token=A_lex(br, pointer, line, false);
									return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
								}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}
					}

				}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}else{
			parser+="20 ";
			return S(br,pointer,line);
		}
	}

	private static Entry<String[],Boolean> E(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if((token.codigo.equals("PARENT") && ((int) token.atributo ==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TABLEID")||token.codigo.equals("TRUE")){
			parser+="21 ";
			String[] resG=G(br,pointer,line).getKey();
			System.out.println("990: " + resG[0]);
			if(!resG[0].equals("errorSin")){
				String[] resJ=J(br,pointer,line).getKey();
				System.out.println("992");
				if(resJ[0].equals("errorSin")){
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
				else{
					System.out.println("996");
					if(resJ[0].equals("null")){return new SimpleEntry<String[],Boolean>(devolverArray(resG[0]),true);}
					else if(resJ[0]=="errorSem"){
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
					}
					else if(resJ[0]=="boolean"){
						if(resG[0]!="boolean"){ 
							genError(30, line[0], resG[0] + "#boolean");
							return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
						}
						return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);
					}
					else{return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);}
				}
			}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}

	}

	private static Entry<String[],Boolean> J(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("AND")){
			parser+="22 ";
			token=A_lex(br, pointer, line, false);
			String [] resG=G(br,pointer,line).getKey();

			if(!resG[0].equals("errorSin"))
			{
				String [] resJ=J(br,pointer,line).getKey();

				if(!resG[0].equals("boolean")) {
					//TIPO_ERROR
					genError(30, line[0], resG[0] + "#boolean");
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
				}else return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);

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

	private static Entry<String[],Boolean> G(BufferedReader br, char[] pointer, int[] line) throws IOException {

		if((token.codigo.equals("PARENT") && ((int) token.atributo ==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TABLEID")||token.codigo.equals("TRUE")){
			parser+="24 ";
			String[] resD=D(br,pointer,line).getKey();
			System.out.println("1050: " + resD[0]);
			if(!resD[0].equals("errorSin"))
			{
				String[] resM=M(br,pointer,line,resD[0]).getKey();
				if(resM[0]=="errorSin"){return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
				else{
					if(resM[0].equals("null")){return new SimpleEntry<String[],Boolean>(devolverArray(resD[0]),true);}
					else if(resM[0]=="errorSem"){
						return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);}
					else{return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);}
				}
			}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}

	private static Entry<String[],Boolean> M(BufferedReader br, char[] pointer, int[] line, String tipo) throws IOException {
		if(token.codigo.equals("EQ")){
			parser+="25 ";
			token=A_lex(br, pointer, line, false);
			String [] resD=D(br,pointer,line).getKey();
			if(!resD[0].equals("errorSin"))
			{
				if(resD[0]!=tipo){
					genError(30, line[0],resD[0]+"#"+ tipo);
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
				}
				else{
					String [] resM=M(br,pointer,line,"boolean").getKey();
					if(resM[0]=="errorSem"){return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);}
					else{return new SimpleEntry<String[],Boolean>(devolverArray("boolean"),true);}
				}
			}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if((token.codigo.equals("PARENT") && ((int) token.atributo ==1))||token.codigo.equals("AND")||token.codigo.equals("COMA")||token.codigo.equals("PYC")){
			parser+="26 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}

	private static Entry<String[],Boolean> D(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if((token.codigo.equals("PARENT") && ((int) token.atributo ==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TABLEID")||token.codigo.equals("TRUE")){
			parser+="27 ";
			Entry<String[],Boolean> resI=I(br,pointer,line);
			System.out.println("1095: "+ resI.getKey()[0]);
			if(!resI.getValue())
			{
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}else {
				Entry<String[],Boolean> resN=N(br,pointer,line);
				System.out.println("1101 " + resN.getValue());
				if(!resN.getValue()){
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
				}
				else{
					if(resN.getKey()[0].equals("null")){return new SimpleEntry<String[],Boolean>(devolverArray(resI.getKey()[0]),true);}
					else if(resN.getKey()[0]=="errorSem"){return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);}
					else if(resN.getKey()[0]=="int"){
						if(resI.getKey()[0]!="int"){
							genError(30, line[0], resI.getKey()[0] +"#int");
							return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);}}
					else{return new SimpleEntry<String[],Boolean>(devolverArray("int"),true);}
				}
			}
		}
		return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);

	}

	private static Entry<String[],Boolean> N(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("MOD")){
			parser+="28 ";
			token=A_lex(br, pointer, line, false);
			String [] resI=I(br,pointer,line).getKey();
			if(!resI[0].equals("int")) {
				genError(30, line[0], resI[0]+"#int");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
			}
			if(!resI[0].equals("errorSin"))
			{
				return N(br,pointer,line);
			}
			else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if((token.codigo.equals("PARENT") && ((int) token.atributo ==1))||token.codigo.equals("AND")||token.codigo.equals("COMA")||token.codigo.equals("PYC")){
			parser+="29 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#%' o ')' o '&&' o ',' o ';");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}
	/**
	 * 
	 * @deprecated
	 */
	private static Entry<String[],Boolean> I(BufferedReader br, char[] pointer, int[] line) throws IOException {
		String[] res=new String[1];
		if(token.codigo.equals("TABLEID")){
			parser+="30 ";
			EntryTS idI=TSControl.getVar((int)token.atributo);
			int lexema=(int) token.atributo;
			funcionTratada[0]=idI.getNombreVar();	//GUARDAMOS NOMBRE FUNCION

			token=A_lex(br, pointer, line, false);

			//SI O FALLA, PETA
			System.out.println("1143 "+ lexema);
			Entry<String[],Boolean> resO=O(br,pointer,line, lexema);
			if(resO.getValue()) {
				if((resO.getKey()[0].equals("null") && idI.getTipo().equals("funcion")) || (!idI.getTipo().equals("funcion") && resO.getKey()[0].equals("null"))) {	//!!!!!!! tipo.funcion no existe aun
					//1133//NO ENTIENDO BIEN ESTE ERROR
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),true);
				}

				res[0]=idI.getTipo();
				return new SimpleEntry<String[],Boolean>(res,true); 				
			}
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),false); 				


		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==0)){
			parser+="31 ";
			token=A_lex(br, pointer, line, false);
			String[]resE=E(br,pointer,line).getKey();
			if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
				token=A_lex(br, pointer, line,false);
				res[0]=resE[0];
				return new SimpleEntry<String[],Boolean>(res,true);	
			}else{
				genError(21, line[0], tokenToString(token) + "#')");
				//continua
				token=A_lex(br, pointer, line,false);
				res[0]=resE[0];
				return new SimpleEntry<String[],Boolean>(res,true);	
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
			return new SimpleEntry<String[],Boolean>(res,true);
		}
	}

	private static Entry<String[],Boolean> O(BufferedReader br, char[] pointer, int[] line, int lexema) throws IOException {
		if(token.codigo.equals("PARENT") && ((int) token.atributo==0)){
			parser+="36 ";
			token=A_lex(br, pointer, line, false);
			if(L(br,pointer,line).getValue()){
				if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
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

	private static Entry<String[],Boolean> T(BufferedReader br, char[] pointer, int[] line) throws IOException {
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
			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> L(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if((token.codigo.equals("PARENT") && ((int) token.atributo==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TRUE")||token.codigo.equals("TABLEID")){
			parser+="41 ";
			Entry<String[],Boolean> resE=E(br, pointer, line);
			EntryTS id=TSControl.getVar(funcionTratada[0].hashCode());

			if(id.getTipoParamXX(CuentaParametros)!=null && id.getTipoParamXX(CuentaParametros).equals(resE.getKey()[0])) {
				CuentaParametros++;		
			} else{
				genError(32, line[0],CuentaParametros +"#" + funcionTratada[0]+"#"+ id.getTipoParamXX(CuentaParametros)+ "#" +resE.getKey()[0] );
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);}


			if(resE.getValue()){

				if(Q(br, pointer, line).getValue()){
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
			parser+="42 ";
			EntryTS id=TSControl.getVar(funcionTratada[0].hashCode());
			if(id.getNumParam()>0) {
				genError(33, line[0], funcionTratada[0]);
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),true);			
			}
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#(' o ')' o 'false' o 'true' o 'cadena' o 'constante' o 'variable");
			return recuperacionError(br, pointer, line);
		}
	}
	/**
	 * @deprecated
	 */
	private static Entry<String[],Boolean> Q(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("COMA")){
			parser+="43 ";
			token=A_lex(br, pointer, line, false);
			Entry<String[],Boolean> resE = E(br, pointer, line);
			EntryTS id=TSControl.getVar(funcionTratada[0].hashCode());

			if(resE.getValue()){
				if(id.getTipoParamXX(CuentaParametros)!=null && id.getTipoParamXX(CuentaParametros).equals(resE.getKey()[0])) {
					CuentaParametros++;		
				} else 	{

					//NO ENTIENDO BIEN ESTE ERROR
					return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),false);
				}
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
			return recuperacionError(br, pointer, line);}
	}

	private static Entry<String[],Boolean> X(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if((token.codigo.equals("PARENT") && ((int) token.atributo==0))||token.codigo.equals("CAD")||token.codigo.equals("CTE")||token.codigo.equals("FALSE")||token.codigo.equals("TRUE")||token.codigo.equals("TABLEID")){
			parser+="45 ";
			String [] resE=E(br, pointer, line).getKey();
			if(!resE[0].equals("errorSin")){
				return new SimpleEntry<String[],Boolean>(devolverArray(resE[0]),true);
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if(token.codigo.equals("PYC")){
			parser+="46 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("void"),true);
		}
		else{
			genError(21, line[0],  tokenToString(token) + "#;' o '(' o 'false' o 'true' o 'cadena' o 'constante' o 'variable");
			return recuperacionError(br, pointer, line);
		}
	}

	private static Entry<String[],Boolean> F(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("TABLEID")){
			parser+="47 ";
			//ASEM
			hashFuncion=(int)token.atributo;
			EntryTS ent= TSControl.getFromGlobal(hashFuncion);
			funcionTratada[0] = ent.getNombreVar();//COGEMOS EL NOMBRE DE LA FUNCION
			System.out.println(funcionTratada[0]);
			TSControl.setParametersFunc(funcionTratada[0],null,null,null,null,"funcion");

			//TSControl.TS.putSimbolo(funcionTratada[0], "funcion");//la aÃ±adimos a la TS Global
			System.out.println("1338 "+funcionTratada[0]);
			TiposParametros=new ArrayList<String>(0);
			token=A_lex(br, pointer, line, false);	
			String TipoFuncion=H(br,pointer,line).getKey()[0];


			System.out.println("1343 "+funcionTratada[0]);
			TSControl.setParametersFunc(funcionTratada[0],TipoFuncion,null,null,null,null);	

			if(!TipoFuncion.equals("errorSin")){
				System.out.println("1347 "+funcionTratada[0]);
				if(token.getCod().equals("PARENT") && (int)token.getAtr()==0){
					token=A_lex(br, pointer, line, false);
					System.out.println("1350 "+funcionTratada[0]);
					if(A(br,pointer,line).getValue()){

						if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
							token=A_lex(br, pointer, line, false);
							if(token.codigo.equals("LLAVE") && ((int) token.atributo ==0)){
								token=A_lex(br, pointer, line, false);
								if(C(br,pointer,line).getValue()){
									if(token.codigo.equals("LLAVE") && ((int) token.atributo==1)){
										token=A_lex(br, pointer, line, false);
										return new SimpleEntry<String[],Boolean>(null,true);
									}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
								}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
							}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
						}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
					}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
				}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}else {return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
	}

	private static Entry<String[],Boolean> H(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("BOOL") || token.codigo.equals("INT") || token.codigo.equals("STR")){
			parser+="48 ";
			TiposParametros.add(tokenToString(token));
			return T(br, pointer, line);
		}
		else if(token.codigo.equals("VOID")){
			token=A_lex(br, pointer, line, false);
			parser+="49 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("void"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#boolean' o 'int' o 'string' o 'void");
			return recuperacionError(br, pointer, line);
		}
	}


	private static Entry<String[],Boolean> A(BufferedReader br, char[] pointer, int[] line) throws IOException {
		System.out.println("1670 "+funcionTratada[0]);
		if(token.codigo.equals("BOOL")||token.codigo.equals("INT")||token.codigo.equals("STR")){
			parser+="50 ";
			Entry<String[],Boolean> resT=T(br, pointer, line);
			if(!resT.getValue()){
				return new SimpleEntry<String[], Boolean>(devolverArray("errorSin"), false);
			}
			TiposParametros.add(resT.getKey()[0]);	//{TiposParametros[CuentaParametrosDec]:=T.tipo;
			if(token.codigo.equals("TABLEID")){
				token=A_lex(br, pointer, line, false);
				K(br, pointer, line);
				return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);

			}else{
				genError(21, line[0],tokenToString(token) + "#variable");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}
		}else if(token.codigo.equals("VOID")){
			parser+="51 ";
			//(String nombreVar, String tipoRetorno, Integer numParam, String[] tipoParamXX, String EtiqFuncion)
			String[] paramTipos= new String[TiposParametros.size()];
			for(int i=0;i<TiposParametros.size();i++){
				paramTipos[i]=TiposParametros.get(i);
			}
			System.out.println("1697 "+funcionTratada[0]);
			TSControl.setParametersFunc(funcionTratada[0],null,0,paramTipos,funcionTratada[0],null);	
			token=A_lex(br, pointer, line, false);
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}else{
			genError(21, line[0], tokenToString(token) + "#boolean' o 'int' o 'string' o 'void");
			return recuperacionError(br, pointer, line);
		}
	}

	private static Entry<String[],Boolean> K(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("COMA")){
			parser+="52 ";
			token=A_lex(br, pointer, line, false);
			String[] resT=T(br, pointer, line).getKey();
			//GESTION ERROR
			TiposParametros.add(resT[0]);//{TiposParametros[CuentaParametrosDec]:=T.tipo;
			if(TiposParametros.size()>100) {
				genError(34, line[0], TiposParametros.size()+"");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSem"),true);
			}

			if(token.codigo.equals("TABLEID")){
				token=A_lex(br, pointer, line, false);
				return K(br, pointer, line);
			}else{ 
				genError(21, line[0],tokenToString(token) + "#variable");
				return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
			}	
		}
		else if(token.codigo.equals("PARENT") && ((int) token.atributo==1)){
			parser+="53 ";

			//{TiposParametros[CuentaParametrosDec]:=T.tipo;
			String[] paramTipos= new String[TiposParametros.size()];
			for(int i=0;i<TiposParametros.size();i++){
				paramTipos[i]=TiposParametros.get(i);
			}
			TSControl.setParametersFunc(funcionTratada[0],null,TiposParametros.size(),paramTipos,funcionTratada[0],null);
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(21, line[0], tokenToString(token) + "#,' o '(");
			return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);
		}
	}

	private static Entry<String[],Boolean> C(BufferedReader br, char[] pointer, int[] line) throws IOException {
		if(token.codigo.equals("GET")||token.codigo.equals("TABLEID")||token.codigo.equals("IF")||token.codigo.equals("LET")||token.codigo.equals("PUT")||token.codigo.equals("RET")){
			parser+="54 ";
			//String [] resB=B(br, pointer, line).getKey();
			Entry<String[],Boolean> resB=B(br, pointer, line);
			if(resB.getValue()){
				Entry<String[],Boolean> resC=C(br, pointer, line);
				if(resC.getValue()){
					return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
				}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
			}else{return new SimpleEntry<String[],Boolean>(devolverArray("errorSin"),false);}
		}
		else if(token.codigo.equals("LLAVE") && ((int) token.atributo==1)){
			parser+="55 ";
			return new SimpleEntry<String[],Boolean>(devolverArray("null"),true);
		}
		else{
			genError(20, line[0], tokenToString(token) + "#get' o 'variable' o 'if' o 'let' o 'put' o 'return' o '}");
			return recuperacionError(br, pointer, line);
		}
	}			

	//FORMATO: A\nB\nC
	private static String [] devolverArray(String arrayacrear) {
		return arrayacrear.split("\n");
	}
}