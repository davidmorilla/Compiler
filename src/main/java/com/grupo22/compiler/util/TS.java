package com.grupo22.compiler.util;
import java.util.HashMap;
import java.util.LinkedList;

public class TS {
	private HashMap<String,Integer> __hm__ts__;
	private HashMap<Integer,String> __lex_to_name;
	private HashMap<Integer,EntryTS> simbolos;
	private LinkedList<Integer> lexemas;
	private String nombreTabla;
	private int despl;

	/** No debe usarse este constructor fuera de la clase TSControl.
	 */
	public TS(String nombreTabla){
		__hm__ts__=new HashMap<String,Integer>();
		__lex_to_name=new HashMap<Integer,String>();
		simbolos=new HashMap<>();
		lexemas=new LinkedList<>();
		this.nombreTabla=nombreTabla;
		despl=0;
	}
	/**
	 * 
	 * @return Devuelve el nombre de la funciÃ³n que creo la tabla o "Global" en caso de que nos encontremos fuera de una funciÃ³n.
	 */
	public String getNombreTabla(){
		return nombreTabla;
	}
	/**
	 * Esta funcion solo debe utilizarla el Analizador Lexico. Si quiere introducirse un simbolo por el AnalizadorSemantico debe usar la funcion putSimbolo()
	 * @see putSimbolo
	 * @param nombre nombre (string) del identificador
	 */
	public void putSimboloLex(String nombre){
		System.out.println("putsimbololex: " + nombre.hashCode());
		__hm__ts__.put(nombre, nombre.hashCode());
		__lex_to_name.put(nombre.hashCode(), nombre);

	}
	/**
	 * Introduce la variable en la TS que se encuentre (local si es dentro de una funcion y global si es fuera de ella).
	 * @param nombreVar nombre (string) de la variable
	 * @param tipo tipo de la variable ("entero","logico";"cadena","funcion")
	 * @return 0 si no existe previamente en la TS local
	 * @return -1 si ya existe en la TS local previamente
	 */
	public int putSimbolo(String nombreVar, String tipo){
		if(existe(nombreVar.hashCode())){
			return -1;
		}
		lexemas.add(nombreVar.hashCode());
		__hm__ts__.put(nombreVar, nombreVar.hashCode());
		__lex_to_name.put(nombreVar.hashCode(), nombreVar);
		simbolos.put(nombreVar.hashCode(),new EntryTS(nombreVar, nombreVar.hashCode(), despl, tipo));
		System.out.println("holahola" + simbolos.toString());
		despl+=getTamTipo(tipo);
		return 0;
	}
	/**
	 * Obtiene el tamaÃ±o de cada tipo de datos.
	 * @param tipo tipo de la variable
	 * @return 2 si es entero o logico
	 * @return 128 si es una cadena
	 * @return 0 si es una funcion
	 * @throws IllegalArgumentException si no es ninguno de los tipos anteriores
	 */  
	private int getTamTipo(String tipo) { //HAY QUE COMPLETAR TAMANOS
		switch(tipo){
		case "boolean":
			return 2;
		case "int":
			return 2;
		case "string":
			return 128;
		case "funcion":
			return 0;
		default:
			throw new IllegalArgumentException("No es un tipo valido\n");
		}
	}
	/**
	 * Obtiene el EntryTS de la variable a partir de su hashcode
	 * @param lexema el hashcode de la variable
	 * @return el nombre de la variable
	 */
	public EntryTS getVar(int lexema){
		System.out.println(simbolos.toString());
		if(existe(lexema)) {
			return simbolos.get(lexema);
		} else if (__lex_to_name.containsKey(lexema)){
			return new EntryTS(__lex_to_name.get(lexema),lexema, -1, null);
		}
		return null;
	}

	/**
	 * Obtiene el nombre(string) de la variable a partir de su hashcode
	 * @param lexema
	 * @return null si no existe y el nombre de la variable tambien.
	 */
	public String getVarName(int lexema){

		System.out.println("98: " +getNombreTabla()+ __lex_to_name.get(lexema));
		return __lex_to_name.get(lexema);
	}
	/**
	 * Devuelve una lista iterable de todos los lexemas (hashcodes) pertenecientes a la tabla de simbolos actual.
	 * @return LinkedList<Integer> de hashcodes de todos los simbolos de la TS
	 */
	public LinkedList<Integer> getLexemas(){
		return lexemas;
	}
	/**
	 * 
	 * @param lexema Hashcode de la variable a buscar
	 * @return true si la variable se encuentra en la TS actual
	 * @return false si la variable no se encuentra en la TS actual
	 * @implSpec En caso de que nos encontremos en una TS local y la variable buscada sea global esta funcion devolvera false. Para que devuelva true en caso de encontrarse en la global debe llamarse a la funcion de la clase TSControl existeLocalGlobal()
	 */
	public boolean existe(int lexema){
		return simbolos.containsKey(lexema);
	}
	/**
	 * Esta funcion solo debe utilizarla el Analizador Lexico. Si se quiere comprobar la existencia de un simbolo por el AnalizadorSemantico debe usarse la funcion existe()
	 * @param nombre nombre(string) de la variable
	 * @return true si existe la variable
	 * @return false si no existe la variable
	 */
	public boolean existeLex(String nombre){
		return __hm__ts__.containsKey(nombre);
	}
	/**
	 * Introduce los parametros en la entrada de la variable
	 * @param nombreVar
	 * @param tipoRetorno
	 * @param numParam
	 * @param tipoParamXX
	 * @param EtiqFuncion
	 */

	/**
	 * No usar esta funcion fuera de la clase TSControl.
	 * @param nombreVar
	 * @param tipoRetorno
	 * @param numParam
	 * @param tipoParamXX
	 * @param EtiqFuncion
	 */
	public void setParameters(String nombreVar,String tipoRetorno, Integer numParam, String[] tipoParamXX, String EtiqFuncion,String tipo){
		//System.out.println(simbolos==null);
		System.out.println(nombreVar==null);
		System.out.println(simbolos.toString());
		EntryTS entry=simbolos.get(nombreVar.hashCode());
		if(tipoRetorno!=null){
			entry.setTipoRetorno(tipoRetorno);
		}
		if(numParam!=null){
			entry.setNumParam(numParam);
		}
		if(tipoParamXX!=null){
			entry.setTipoParamXX(tipoParamXX);
		}
		if(EtiqFuncion!=null){
			entry.setEtiqFuncion(EtiqFuncion);
		}
		if(tipo!=null) {
			entry.setTipo(tipo);
		}

		simbolos.put(nombreVar.hashCode(), entry);
	}
	public String toString() {
		return __lex_to_name.toString();
	}
}
