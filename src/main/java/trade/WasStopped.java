/*
 Copyright (C) 2012 Albert Steiner
 Copyright (C) 2022 Albert Steiner

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package trade;

/**
 *
 * @author albert
 */
public class WasStopped extends RuntimeException {

  WasStopped() {
  }

  WasStopped(String message) {
    super(message);
    /*
    System.err.flush();
   System.err.flush();
   System.err.flush();
   System.err.flush();
   System.err.flush();System.err.flush();
   System.err.flush();
   System.exit(-22);
*/
  }
}
