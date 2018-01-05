/*
* Copyright (C) 2014-2018 by IFPEN
* All rights reserved.
*
* IFPEN Headquarters:
* 1 & 4, avenue de Bois-Preau
* 92852 Rueil-Malmaison Cedex - France
*/
package stratifx.canvas.graphics.tooltip;

/**
 *
 * @author lecomtje
 */
public class GTooltipInfo implements ITooltipInfo {
    
    @Override
    public String getInfo(int x, int y) {
        return info;
    }
    
    public void setInfo(String info) {
        this.info = info;
    }
        
    
    String info;
    
}