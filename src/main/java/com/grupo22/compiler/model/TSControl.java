package com.grupo22.compiler.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.grupo22.compiler.util.EntryTS;
import com.grupo22.compiler.util.TS;


public class TSControl {
    private HashMap<Integer,TS> todasTS;
    private int indiceTSActual;
    /** Es la tabla de simbolos actual. Se actualiza automaticamente cada vez que se usan las funciones createTS() y destroyTS()
     */
    public TS TS;
    private BufferedWriter tablaSW;
    private FileWriter tablaS;
    
    /** Crea un controlador de tablas de simbolos. Crea una tabla de simbolos global. Abre el buffer de escritura para cuando se quiera imprimir alguna TS.
     */
    public TSControl(){
        todasTS=new HashMap<>();
        TS=new TS("GLOBAL");
        todasTS.put(0, TS);
        indiceTSActual=0;
        try {
            tablaS = new FileWriter("C:\\Users\\Usuario\\Desktop\\Universidad\\Tercero\\Procesadores de Lenguajes\\tablaSimbolos.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tablaSW = new BufferedWriter(tablaS);
    }
    /** @param nombreFuncion nombre (string) de la funcion que crea la tabla de simbolos
     *   @implSpec Automaticamente actualiza TS a la tabla de simbolos que acaba de crearse
     */
    public void createTS(String nombreFuncion) {
        TS=new TS(nombreFuncion);
        indiceTSActual++;
        todasTS.put(indiceTSActual, TS);
    }
    /** @throws IllegalStateException en caso de que se intente eliminar la tabla de simbolos global
     *  @implSpec Automaticamente actualiza TS a la tabla de simbolos global e imprime en el buffer de escritura abierto la tabla de simbolos local que acaba de destruirse
     */
    public void destroyTS() throws IllegalStateException{
        printTS();
        indiceTSActual--;
        if(indiceTSActual<0){
            throw new IllegalStateException("No se puede eliminar la tabla de símbolos global\n");
        }
        TS=todasTS.get(indiceTSActual);
    }
    /** Imprime la tabla de símbolos actual en el buffer de escritura abierto
     */
    public void printTS() {
        //por implementar
        try {
            if(indiceTSActual==0){
                tablaSW.write("TABLA GLOBAL #"+indiceTSActual+":\n");
            } else{
                tablaSW.write("TABLA DE LA FUNCION '"+ TS.getNombreTabla()+"' #"+indiceTSActual+":\n");
            }
            
            for(Integer var : TS.getLexemas()) {
				tablaSW.write("* LEXEMA : '"+TS.getVar(var).getNombreVar()+"'\n\tAtributos:\n\t+tipo:\t\t'"+TS.getVar(var).getTipo()+"'\n\t+despl:\t\t"+TS.getVar(var).getDespl()+"\n");
                int np=TS.getVar(var).getNumParam();
                if(np!=-1){
                    tablaSW.write("\t+numParam:\t\t" + np + "\n");
                    for(int i=0; i<np; i++){
                        tablaSW.write("\t+tipoParam " + i+1 +":\t\t'" + TS.getVar(var).getTipoParamXX(i) + "'\n");
                    }
                    tablaSW.write("\t+tipoRetorno:\t\t'"+ TS.getVar(var).getTipoRetorno() + "'\n");
                }
            }
            tablaSW.write("-------------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /** @param lexema hashcode del identificador
     *  @return true si existe en la tabla de simbolos local
     *  @return true si no existe en la tabla de simbolos local pero si en la global
     *  @return false si no existe ni en la tabla de simbolos local ni en la global
     */
    public boolean existeLocalOGlobal(int lexema){
        boolean found=false;
        found=todasTS.get(indiceTSActual).existe(lexema);
        if(!found){
            found=todasTS.get(0).existe(lexema);
        }
        return found;
    }
    /** @param lexema hashcode del identificador
     *  @return EntryTS con toda la información acerca de ese identificador
     *  @return null si el id no existe ni en la tabla de simbolos local ni en la global
     *  @implSpec En caso de que existiese el id tanto en la ts local como la global se devuelve el de la local
     */
    public EntryTS getClosestVar(int lexema){
        int index=indiceTSActual;
        EntryTS found=null;
        if(todasTS.get(index)!=null)
             found=todasTS.get(index).getVar(lexema);
        if(found==null){
            index=0;
            if(todasTS.get(index)!=null)
                found=todasTS.get(index).getVar(lexema);
            }
            return found;
        }
    
    
    public EntryTS getFromGlobal(int lexema){
            return todasTS.get(0).getVar(lexema);
        }
     public String getNameFromGlobal(int lexema){
            return todasTS.get(0).getVarName(lexema);
        }
    /** 
     * Solo debe usarse si no estamos en la ts global
     * @param nombreVar nombre (string) del identificador
     *  @implSpec se añade el simbolo a la TS global como entero
     */
    public void putSimboloEnGlobal(String nombreVar, String Tipo){
        TS lTS=todasTS.get(0);
        lTS.putSimbolo(nombreVar, Tipo);
        todasTS.put(0, lTS);
        if(indiceTSActual==0){
            TS=todasTS.get(0);
        }
    }
    /** Cierra el buffer de escritura 
     */
    public void closeWritingBuffer(){
        try {
            tablaSW.close();
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
    public void setParametersFunc(String nombreVar, String tipoRetorno, Integer numParam, String[] tipoParamXX, String EtiqFuncion,String tipo){
        TS aTS=todasTS.get(0);    
        aTS.setParameters(nombreVar, tipoRetorno, numParam, tipoParamXX, EtiqFuncion,tipo);
        todasTS.put(0, aTS);
    }
}
    
