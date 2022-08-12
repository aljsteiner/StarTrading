/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trade;

/**
 *
 * @author albert
 */
public class WasFatalError extends RuntimeException {

  WasFatalError() {
  }

  WasFatalError(String message) {
    super(message);
  }
}
