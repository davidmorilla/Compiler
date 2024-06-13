package com.grupo22.compiler.util;
public class Token {    //GENERAR UN PAIR (SI ES NULL, SE PONE STRING NULO
    public String codigo;
    public Object atributo;

    public Token(String codigo, Object atributo) {
        this.codigo=codigo;
        this.atributo=atributo;
    }
    public String getCod () {        //CUIDADO PORQUE DEBE SER SHORT
        return codigo;
    }
    public Object getAtr () {        //CUIDADO PORQUE DEBE SER SHORT
        return atributo;
    }
    public String toString() {
    	return codigo + ", " + atributo;
    }
}