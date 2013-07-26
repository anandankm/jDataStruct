/**
 *                  GNU GENERAL PUBLIC LICENSE
 *
 *  Copyright (C) 2013.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grooveshark.jdatastruct.graph.sample;


/**
 * Exception to be thrown, when there is any discrepancies in
 * {@link com.grooveshark.util.command}
 *
 * @author andy.compeer@gmail.com
 */

public class Neo4jRestException extends Exception {


    private static final long serialVersionUID = 7526472295622776147L;


    public Neo4jRestException() {
        super();
    }

    public Neo4jRestException(String msg) {
        super(msg);
    }

    public Neo4jRestException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public Neo4jRestException(Throwable cause) {
        super(cause);
    }
}
