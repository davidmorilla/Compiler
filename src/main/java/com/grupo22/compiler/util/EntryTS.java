package com.grupo22.compiler.util;

import java.util.ArrayList;
import java.util.List;

public class EntryTS {
    private String tipo;
    private int numParam;
    private List<String> tipoParamXX;
    private String tipoRetorno;
    private String EtiqFuncion;
    private int despl;
    private int lexema;
    private String nombreVar;

    /**
     * Crea una instancia para una variable. Solo debe ser llamada dentro de la clase TSControl o TS.
     * @param nombreVar Nombre (string) de la variable
     * @param lexema Hashcode de la variable
     * @param despl Desplazamiento de la variable o funcion respecto a la tabla de simbolos donde se encuentra
     * @param tipo Solo puede ser: "entero","logico","cadena" o"funcion"
     */
    public EntryTS(String nombreVar, int lexema, int despl, String tipo){
        this.tipo=tipo;
        this.nombreVar=nombreVar;
        this.lexema=lexema;
        this.despl=despl;
        tipo=null;
        numParam=-1;
        tipoParamXX=null;
        tipoRetorno=null;
        EtiqFuncion=null;
    }
    public void setTipo(String tipo) {
    	this.tipo=tipo;
    }
    /**
     * 
     * @return Devuelve el tipo ("entero","logico","cadena","funcion")
     */
    public String getTipo() {
        return tipo;
    }
    /**
     * 
     * @return Un entero comprendido entre 0 y MAX_INT incluidos si el tipo de la variable es "funcion"
     * @return -1 si el tipo de la variable es diferente de "funcion"
     */
    public int getNumParam() {
        return numParam;
    }
    /**
     * Establece el numero de parametros de entrada de la funcion
     * @param numParam 
     */
    public void setNumParam(int numParam) {
        this.numParam = numParam;
        this.tipoParamXX= new ArrayList<String>(numParam);
    }
    /**
     * 
     * @param index indice del parametro
     * @return El tipo del parametro cuya posicion es index
     */
    public String getTipoParamXX(int index) {
        return tipoParamXX.get(index);
    }
    /**
     * Introduce el tipo de los parametros de entrada de una funcion
     * @param index indice del parametro cuyo tipo se quiere introducir
     * @param tipo tipo del parametro cuya posicion es index
     */
    public void setTipoParamXX(int index, String tipo) {
        this.tipoParamXX.add(index, tipo);
    }

    public void setTipoParamXX(List<String> params){
        for(int i=0;i<numParam;i++){
            tipoParamXX.add(i,params.get(i));
        }
    }
    /**
     *
     * @return El tipo de retorno de la funcion
     */
    public String getTipoRetorno() {
        return tipoRetorno;
    }
    /**
     * Establece el tipo de retorno de la funcion
     * @param tipoRetorno tipo de retorno de la funcion
     */
    public void setTipoRetorno(String tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }
    /**
     * 
     * @return La etiqueta asociada a la funcion
     */
    public String getEtiqFuncion() {
        return EtiqFuncion;
    }
    /**
     * Asocia una etiqueta a la funcion
     * @param etiqFuncion etiqueta que quiere asociarse a la funcion
     */
    public void setEtiqFuncion(String etiqFuncion) {
        EtiqFuncion = etiqFuncion;
    }
    /**
     *
     * @return El desplazamiento de esa variable o funcion respecto a su tabla de simbolos.
     */
    public int getDespl() {
        return despl;
    }
    /**
     * 
     * @return El hashcode de la variable o funcion
     */
    public int getLexema() {
        return lexema;
    }
    /**
     * 
     * @return El nombre (string) de la variable o funcion
     */
    public String getNombreVar() {
        return nombreVar;
    }
    public String toString() {
    	return nombreVar;
    }
    
}
