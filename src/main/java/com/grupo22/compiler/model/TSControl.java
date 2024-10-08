package com.grupo22.compiler.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.grupo22.compiler.util.EntryTS;
import com.grupo22.compiler.util.TS;


public class TSControl {
	/** Es la tabla de simbolos actual. Se actualiza automaticamente cada vez que se usan las funciones createTS() y destroyTS()
	 */
	private TS currentTS;
	private TS globalTS;
	private boolean isGlobal;
	private static int contadorTS;
	private BufferedWriter TSBufferedWriter;
	private FileWriter TSFileWriter;
	public final static String TS_OUTPUT_FORMAT = "src/main/java/com/grupo22/compiler/output/ts_output%d.txt";
	/** Crea un controlador de TSFileWriter de simbolos. Crea una tabla de simbolos global. Abre el buffer de escritura para cuando se quiera imprimir alguna TS.
	 */
	public TSControl(int code_number){
		String TS_OUTPUT = String.format(TS_OUTPUT_FORMAT, code_number);
		globalTS=new TS("GLOBAL");
		currentTS= globalTS;
		isGlobal=true;
		contadorTS =0;
		try {
			TSFileWriter = new FileWriter(TS_OUTPUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		TSBufferedWriter = new BufferedWriter(TSFileWriter);
	}
	/** @param nombreFuncion nombre (string) de la funcion que crea la tabla de simbolos
	 * 	@throws IllegalStateException si se declara una función dentro de otra
	 *  @implSpec Automaticamente actualiza TS a la tabla de simbolos que acaba de crearse
	 */
	public boolean isGlobal() {
		return isGlobal;
	}
	public void createTS(String nombreFuncion) throws IllegalStateException{
		//System.out.println("SE HA CREADO 1");
		if(isGlobal) {
			currentTS=new TS(nombreFuncion);
			isGlobal=false;
			contadorTS++;
		}else {
			throw new IllegalStateException("No puede declararse una función dentro de otra función");
		}
	}
	/** @throws IllegalStateException en caso de que se intente eliminar la tabla de simbolos global
	 *  @implSpec Automaticamente actualiza TS a la tabla de simbolos global e imprime en el buffer de escritura abierto la tabla de simbolos local que acaba de destruirse
	 */
	public void destroyTS() throws IllegalStateException{
		//System.out.println("SE HA BORRADO 1" +isGlobal);

		printTS();
		//isGlobal=true;
		if(isGlobal){
			throw new IllegalStateException("No se puede eliminar la tabla de símbolos global\n");
		}
		isGlobal=true;
		currentTS=globalTS;
	}
	/** Imprime la tabla de símbolos actual en el buffer de escritura abierto
	 */
	public void printTS() {
		//por implementar
		try {
			if(isGlobal){
				TSBufferedWriter.write("TABLA GLOBAL # 0 :\n");
			} else{
				TSBufferedWriter.write("TABLA LOCAL DE LA FUNCION "+ currentTS.getNombreTabla()+" # "+contadorTS+" :\n");
			}

			for(Integer var : currentTS.getLexemas()) {
				if(currentTS.getVar(var).getTipo().equals("function") ) {
					TSBufferedWriter.write("* LEXEMA : '"+currentTS.getVar(var).getNombreVar()+"'\n\tAtributos :\n\t+tipo :\t\t'"+currentTS.getVar(var).getTipo()+"'\n");
				}else {
					TSBufferedWriter.write("* LEXEMA : '"+currentTS.getVar(var).getNombreVar()+"'\n\tAtributos :\n\t+tipo :\t\t'"+currentTS.getVar(var).getTipo()+"'\n\t+despl:\t\t"+currentTS.getVar(var).getDespl()+"\n");
				}
				int np=currentTS.getVar(var).getNumParam();
				if(np!=-1){
					TSBufferedWriter.write("\t+numParam :\t\t" + np + "\n");
					for(int i=0; i<np; i++){
						TSBufferedWriter.write("\t\t+tipoParam" + (i+1) +" :\t\t'" + currentTS.getVar(var).getTipoParamXX(i) + "'\n");
					}
					TSBufferedWriter.write("\t+tipoRetorno :\t\t'"+ currentTS.getVar(var).getTipoRetorno() + "'\n");
				}
			}
			TSBufferedWriter.write("-------------------------------------------\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/** @param lexema hashcode del identificador
	 *  @return 1 si existe en la tabla de simbolos local
	 *  @return 0 si no existe en la tabla de simbolos local pero si en la global
	 *  @return -1 si no existe ni en la tabla de simbolos local ni en la global
	 */
	public int existe(int lexema){
		if (isGlobal) {
			return currentTS.existe(lexema)? 0 : -1;
		} else if(currentTS.existe(lexema)){
			return 1;
		} else {
			return globalTS.existe(lexema)? 0 : -1;
		}
	}

	/** @param lexema hashcode del identificador
	 *  @return 1 si existe en la tabla de simbolos local
	 *  @return 0 si no existe en la tabla de simbolos local pero si en la global
	 *  @return -1 si no existe ni en la tabla de simbolos local ni en la global
	 */
	public int existeLex(String lexema){
		if (isGlobal) {
			return currentTS.existeLex(lexema)? 0 : -1;
		} else if(currentTS.existeLex(lexema)){
			return 1;
		} else {
			return globalTS.existeLex(lexema)? 0 : -1;
		}
	}
	/** @param lexema hashcode del identificador
	 *  @return EntryTS con toda la información acerca de ese identificador
	 *  @return null si el id no existe ni en la tabla de simbolos local ni en la global
	 *  @implSpec En caso de que existiese el id tanto en la ts local como la global se devuelve el de la local
	 */
	public EntryTS getVar(int lexema){
		int existe = existe(lexema);
		if(existe ==-1) {
			return null;
		}else if(existe == 0){
			return globalTS.getVar(lexema);
		} else {
			return currentTS.getVar(lexema);
		}
	}


	public EntryTS getFromGlobal(int lexema){
		return globalTS.getVar(lexema);
	}
	public String getNameFromGlobal(int lexema){
		return globalTS.getVarName(lexema);
	}
	/** 
	 * Solo debe usarse si no estamos en la ts global
	 * @param nombreVar nombre (string) del identificador
	 *  @implSpec se añade el simbolo a la TS global como entero
	 */
	public void putSimboloEnGlobal(String nombreVar, String Tipo){
		globalTS.putSimbolo(nombreVar, Tipo);
	}
	public void putSimbolo(String nombreVar, String Tipo){
		currentTS.putSimbolo(nombreVar, Tipo);
	}
	public void putSimbolo(String nombreVar){
		System.out.println(currentTS.toString() + "\n Llamamos a currentTS.putSimboloLex(String)");
		currentTS.putSimboloLex(nombreVar);
		System.out.println(currentTS.toString());
	}
	public void putSimbolo(int lexema, String Tipo){
		//System.out.println("varname::" + currentTS.getVarName(lexema));
		currentTS.putSimbolo(currentTS.getVarName(lexema), Tipo);
	}
	
	public void setTipoGlobal(int lexema, String tipo)
	{
		globalTS.setTipo(lexema, tipo);
	}
	
	public void setTipo(int lexema, String tipo)
	{
		currentTS.setTipo(lexema, tipo);
	}

	public void setNombreTabla(String nombreTabla) {
		if(!isGlobal) {
			currentTS.setNombreTabla(nombreTabla);
		} else {
			System.err.println("No se puede cambiar de nombre a la tabla de símbolos global");
		}
	}
	/** Cierra el buffer de escritura 
	 */
	public void closeWritingBuffer(){
		try {
			TSBufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Establece los parametros de una funcion en la TS global. Los parametros pueden ser null en caso de que no se quieran añadir en ese momento.
	 * @param nombreVar no puede ser null
	 * @param tipo 
	 * @param tipoRetorno
	 * @param numParam
	 * @param tipoParamXX
	 * @param EtiqFuncion
	 */
	public void setParametersFunc(String nombreVar, String tipoRetorno, Integer numParam, List<String> tipoParamXX, String EtiqFuncion,String tipo){
		globalTS.setParameters(nombreVar, tipoRetorno, numParam, tipoParamXX, EtiqFuncion,tipo);
	}

	public String getVarName(int lexem) {
		// TODO Auto-generated method stub
		return currentTS.getVarName(lexem);
	}
}

