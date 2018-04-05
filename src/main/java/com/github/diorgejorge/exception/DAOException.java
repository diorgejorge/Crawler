package com.github.diorgejorge.exception;

/**
 * Created by Diorge Jorge on 21/03/2018.
 */
public class DAOException extends CoreException{
    public DAOException(String s) {
        super(s);
    }
    public DAOException(Throwable cause) {
        super(cause);
    }

}
