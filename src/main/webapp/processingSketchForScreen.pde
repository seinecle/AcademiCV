
        //COLORS

        String BACKGROUND_COLOR = "#FFFFFF"; //white

        color backgroundColor = color(hexToRgb(BACKGROUND_COLOR).r,hexToRgb(BACKGROUND_COLOR).g,hexToRgb(BACKGROUND_COLOR).b);
        //color backgroundColor = (12,50,180); //random for test

        int BACKGROUND_ALPHA = 255;
        int MAXSTROKEWEIGHT = 20;





        //SKETCH SIZE

        float SKETCH_WIDTHf = 800f;
        float SKETCH_HEIGTHf = 800f;
        float SKETCH_CENTER_X = SKETCH_WIDTHf / 2;
        float SKETCH_CENTER_Y = SKETCH_HEIGTHf / 2;





        //SPACE BETWEEN SEGMENTS
        float INTERSTICE_SEGMENT = 0.2f;

        //SPACE BETWEEN LABELS AND CIRCLE
        float INTERSTICE_LABEL = 7f;

        //SPACE BETWEEN BEZIER CURVES AND CIRCLES
        float INTERSTICE_BEZIER = 7f;


        //LABEL FOR MAIN SEGMENT
        String mainSegmentLabel;

        class Segment{
            
            String label;
            int count;
            boolean isMain;
            
            Segment (String label, int count, boolean isMain){
                this.label = label;
                this.count = count;
                this.isMain = isMain;
                if (isMain == true){
                    mainSegmentLabel = label;
                }
            }

            String toString(){
                return (this.label);
            }


       }

       
        ArrayList segments; 
        segments = new ArrayList();

       void addSegment(String label,int count,boolean isMain){
          segments.add(new Segment(label,count,isMain));
        }

       void resetSegments(){
          segments = new ArrayList();
        }

        Colors palette;
        String currColor;

void setup() {



//        background(BACKGROUND_COLOR, BACKGROUND_ALPHA);


        background(backgroundColor, BACKGROUND_ALPHA);
        size(SKETCH_WIDTHf, SKETCH_HEIGTHf);
        smooth();
        noStroke();
        palette = new Colors();


}


void draw() {


        //NUMBER SEGMENTS
        float nbSegments = (float) segments.size() - 1;


        //RESIZE SKETCH ACCORDINGLY
        SKETCH_WIDTHf = 600f + nbSegments*7;
        SKETCH_HEIGTHf = SKETCH_WIDTHf;
        SKETCH_CENTER_X = SKETCH_WIDTHf / 2;
        SKETCH_CENTER_Y = SKETCH_HEIGTHf / 2;

        //REDRAW THE SKETCH AT THESE NEW DIMENSIONS
        size(SKETCH_WIDTHf, SKETCH_HEIGTHf);

        //TEXT SIZE
        textSize(12+ (SKETCH_WIDTHf-600)/100 - nbSegments/20);
        textAlign(CENTER,BOTTOM);


        //CIRCLE SIZE

        float OUTER_CIRCLE_DIAMETER = SKETCH_WIDTHf * (float)3/5;
        float INNER_CIRCLE_DIAMETER = OUTER_CIRCLE_DIAMETER*9/10;
        float OUTER_CIRCLE_RADIUS = OUTER_CIRCLE_DIAMETER / 2;
        float INNER_CIRCLE_RADIUS = INNER_CIRCLE_DIAMETER / 2;



        float nbBasicUnitsSegments = 0;
        for (i = 0;i<segments.size();i++){
            if (segments.get(i).isMain==true)continue;
            nbBasicUnitsSegments = nbBasicUnitsSegments+segments.get(i).count;
        }
//        println("total number of basic units, summing all basic units of all segments: "+nbBasicUnitsSegments);
//        println("number of segments (not counting the main one): "+nbSegments);





        //the segment for the author should be between 1/8 and 1/3 of a circle. Between that, it varies with the number of partners.
        float radiusUnitAuthor = max((float) 2 / 16, min(1f / nbSegments, (float) 1 / 3));
 //       println("radius Main is:" + radiusUnitAuthor);

        //WARNING this unit does not represent the size of a segment for one partner, but the atomic unit for such a size.
        // => a partner with 5 relations with the author will have a segment size of 5 x this atomic unit

        float radiusUnitSegment;
        radiusUnitSegment = (1 - INTERSTICE_SEGMENT) * ((1 - radiusUnitAuthor) / (nbBasicUnitsSegments));
 //      println("radius Segment is: "+ radiusUnitSegment);

        float interstice;

        interstice = (1 - (radiusUnitSegment * nbBasicUnitsSegments + radiusUnitAuthor)) / (nbSegments + radiusUnitAuthor + 1);

        // DRAWING MAIN SEGMENT ---------------------


        //SEGMENT ITSELF
        fill(255, 0, 0);
        arc(SKETCH_WIDTHf / 2f, SKETCH_HEIGTHf / 2f, OUTER_CIRCLE_DIAMETER, OUTER_CIRCLE_DIAMETER, TWO_PI * (0.5f - radiusUnitAuthor / 2f), TWO_PI * (float) (0.5f + radiusUnitAuthor / 2f));


        //NAME ATTACHED TO THE MAIN SEGMENT
        
        float sw = textWidth(mainSegmentLabel);
//        println("mainSegmentLabel: "+mainSegmentLabel);
        text(mainSegmentLabel, -sw + (SKETCH_CENTER_X + cos(TWO_PI * 8f / 16f) * OUTER_CIRCLE_RADIUS) - INTERSTICE_LABEL, SKETCH_CENTER_Y + (sin(TWO_PI * 8f / 16f) * OUTER_CIRCLE_RADIUS)- INTERSTICE_LABEL);


        // DRAWING OTHER SEGMENTS -------------

        float countSegments = 0f;
        float countBasicUnits = 0f;

        String segmentLabel;
        for (int i = 0; i < nbSegments; i++) {

            if (segments.get(i).isMain == true) continue;

            float currCountBasicUnits = (float)segments.get(i).count;


            //first the outer arc in a CUSTOM COLOR
           segmentColor = palette.listColors.get(round(countSegments));
           color rgb = color(hexToRgb(segmentColor).r,hexToRgb(segmentColor).g,hexToRgb(segmentColor).b);
           fill(rgb);


            float startArc = TWO_PI * (0.5f + radiusUnitAuthor / 2f + interstice * (countSegments + 1f) + radiusUnitSegment * countBasicUnits);
            float endArc   = TWO_PI * (0.5f + radiusUnitAuthor / 2f + interstice * (countSegments + 1f) + radiusUnitSegment * (countBasicUnits + currCountBasicUnits));
//            println("nb of basic units in curr segment: "+currCountBasicUnits);
//            println("startArc: "+startArc);
//            println("endArc: "+endArc);


            arc(SKETCH_WIDTHf / 2, SKETCH_HEIGTHf / 2, OUTER_CIRCLE_DIAMETER, OUTER_CIRCLE_DIAMETER,startArc,endArc);


            countSegments = countSegments + 1f;
            countBasicUnits = countBasicUnits + currCountBasicUnits;
//            println("accumulated count of total basic units treated so far: " + countBasicUnits);
        }

        countSegments = 0f;
        countBasicUnits = 0f;



        // DRAWING CENTER IN PLAIN COLOR
            fill(255, 255, 255); //in WHITE
        ellipse(SKETCH_WIDTHf/2f,SKETCH_HEIGTHf/2f,INNER_CIRCLE_DIAMETER,INNER_CIRCLE_DIAMETER);
            fill(0, 0, 0); //restore fill color to black


        // DRAWING LABELS FOR OTHER SEGMENTS    

        for (int i = 0; i < nbSegments; i++) {

            if (segments.get(i).isMain == true) continue;
            //orientation of the label.

            float theta = TWO_PI * (0.5f + radiusUnitAuthor / 2 + interstice * (countSegments + 1) + radiusUnitSegment * countBasicUnits + radiusUnitSegment * segments.get(i).count / 2);
            float thetaLabel;

 //           println("theta: " + theta);
 //           println("segment: " + segments.get(i).label);

            float minLimit = (3f / 4f * TWO_PI);
            float maxLimit = (5f / 4f * TWO_PI);

            //this condition orients the labels for easier read
            if (theta < minLimit | theta > maxLimit) {
                thetaLabel = theta + PI;
                textAlign(RIGHT,BOTTOM);
            } else {
                thetaLabel = theta;
                textAlign(LEFT,TOP);
            }


            //COORDINATES OF THE LABEL FOR THE CURRENT SEGMENT
            float x = (SKETCH_CENTER_X
                    + cos(theta)
                    * (OUTER_CIRCLE_RADIUS + INTERSTICE_LABEL));
            float y = SKETCH_CENTER_Y
                    + sin(theta)
                    * (OUTER_CIRCLE_RADIUS + INTERSTICE_LABEL);

            float x_inner = (SKETCH_CENTER_X
                    + cos(theta)
                    * (INNER_CIRCLE_RADIUS - INTERSTICE_BEZIER));
            float y_inner = SKETCH_CENTER_Y
                    + sin(theta)
                    * (INNER_CIRCLE_RADIUS - INTERSTICE_BEZIER);


            // DRAWING THE LABELS, at their correct orientation 

            pushMatrix();
            translate(x, y);
            rotate(thetaLabel);
            text(segments.get(i).label, 0, 0);
            popMatrix();




            // BEZIER CURVE between author and partner

            float strokeThickness = (float) segments.get(i).count * MAXSTROKEWEIGHT / segments.size();
            noFill();
            stroke(255, 0, 0);
            strokeWeight(strokeThickness);


            float x1 = SKETCH_CENTER_X + cos(PI) * (INNER_CIRCLE_RADIUS - INTERSTICE_BEZIER);
            float y1 = SKETCH_CENTER_Y + sin(PI) * (INNER_CIRCLE_RADIUS - INTERSTICE_BEZIER);
            float x2 = (x_inner);
            float y2 = (y_inner);

            float hx1 = x1 + (x2 - x1) * (1f / 4f);
            float hy1 = y1 + ((y2 - y1) / ((x2 - x1)) * (hx1 - x1)) - ((y2 - y1) / 4f);
            float hx2 = x1 + (x2 - x1) * (4f / 5f);
            float hy2 = (y1 + ((y2 - y1) / (x2 - x1)) * (hx2 - x1)) - ((y2 - y1) / 3f);

//            println("hx2: " + hx2);
//            println("hy2: " + hy2);


            bezier(x1, y1, hx1, hy1, hx2, hy2, x2, y2);
            noStroke();

            
//            println("segment size: "+segments.size());
//            println("curr i: "+i);
//            println("curr segment is: "+segments.get(i));

            countSegments = countSegments + 1f;
            countBasicUnits = countBasicUnits + segments.get(i).count;

        }

        noLoop();

    }


class Colors {

ArrayList listColors;

Colors(){
        listColors = new ArrayList();
        listColors.add("#E78B27");
        listColors.add("#6130D5");
        listColors.add("#7FE5E5");
        listColors.add("#59E341");
        listColors.add("#2B2342");
        listColors.add("#E03C86");
        listColors.add("#3D5D21");
        listColors.add("#A48BD9");
        listColors.add("#E7B39C");
        listColors.add("#71211B");
        listColors.add("#DDE238");
        listColors.add("#D1E693");
        listColors.add("#E565DE");
        listColors.add("#E7432B");
        listColors.add("#3F837B");
        listColors.add("#50B376");
        listColors.add("#A5953F");
        listColors.add("#4596BF");
        listColors.add("#A1606A");
        listColors.add("#422592");
        listColors.add("#E898C3");
        listColors.add("#90613D");
        listColors.add("#C6D4DE");
        listColors.add("#501C65");
        listColors.add("#9B87A3");
        listColors.add("#233119");
        listColors.add("#5A1B37");
        listColors.add("#9E3397");
        listColors.add("#E1BB39");
        listColors.add("#E47772");
        listColors.add("#BE31ED");
        listColors.add("#78E37D");
        listColors.add("#2F507D");
        listColors.add("#405EB4");
        listColors.add("#86A52E");
        listColors.add("#BDEABF");
        listColors.add("#6159D8");
        listColors.add("#5C99E6");
        listColors.add("#1D3940");
        listColors.add("#E0375F");
        listColors.add("#E57647");
        listColors.add("#4AB238");
        listColors.add("#65E9B6");
        listColors.add("#A0E13A");
        listColors.add("#EBC787");
        listColors.add("#89AA5E");
        listColors.add("#D6BAE3");
        listColors.add("#DE65B5");
        listColors.add("#C28540");
        listColors.add("#8D8457");
        listColors.add("#A4998C");
        listColors.add("#9A46CC");
        listColors.add("#962D70");
        listColors.add("#7C50A9");
        listColors.add("#AF3539");
        listColors.add("#DF31AD");
        listColors.add("#C5DE65");
        listColors.add("#58BAA8");
        listColors.add("#693D65");
        listColors.add("#6B5558");
        listColors.add("#A63B5C");
        listColors.add("#471F16");
        listColors.add("#3F802C");
        listColors.add("#4F4518");
        listColors.add("#E2DDC0");
        listColors.add("#336B48");
        listColors.add("#AE4418");
        listColors.add("#A560A2");
        listColors.add("#6A7EEB");
        listColors.add("#2A2E6A");
        listColors.add("#DA6E97");
        listColors.add("#D79096");
        listColors.add("#595842");
        listColors.add("#DFB8C3");
        listColors.add("#AE7F6E");
        listColors.add("#8FBA8C");
        listColors.add("#97B9B1");
        listColors.add("#6F85BB");
        listColors.add("#2D6A85");
        listColors.add("#D336D7");
        listColors.add("#6FC6E9");
        listColors.add("#DF9CE3");
        listColors.add("#33282A");
        listColors.add("#C779E1");
        listColors.add("#655E9A");
        listColors.add("#7E4840");
        listColors.add("#61A5B3");
        listColors.add("#794911");
        listColors.add("#BDB486");
        listColors.add("#396062");
        listColors.add("#9F6A90");
        listColors.add("#595973");
        listColors.add("#756F22");
        listColors.add("#A75837");
        listColors.add("#D79B69");
        listColors.add("#7B878B");
        listColors.add("#B5841D");
        listColors.add("#A3B0DC");
        listColors.add("#648B69");
        listColors.add("#DCC168");
        listColors.add("#E78B27");
        listColors.add("#6130D5");
        listColors.add("#7FE5E5");
        listColors.add("#59E341");
        listColors.add("#2B2342");
        listColors.add("#E03C86");
        listColors.add("#3D5D21");
        listColors.add("#A48BD9");
        listColors.add("#E7B39C");
        listColors.add("#71211B");
        listColors.add("#DDE238");
        listColors.add("#D1E693");
        listColors.add("#E565DE");
        listColors.add("#E7432B");
        listColors.add("#3F837B");
        listColors.add("#50B376");
        listColors.add("#A5953F");
        listColors.add("#4596BF");
        listColors.add("#A1606A");
        listColors.add("#422592");
        listColors.add("#E898C3");
        listColors.add("#90613D");
        listColors.add("#C6D4DE");
        listColors.add("#501C65");
        listColors.add("#9B87A3");
        listColors.add("#233119");
        listColors.add("#5A1B37");
        listColors.add("#9E3397");
        listColors.add("#E1BB39");
        listColors.add("#E47772");
        listColors.add("#BE31ED");
        listColors.add("#78E37D");
        listColors.add("#2F507D");
        listColors.add("#405EB4");
        listColors.add("#86A52E");
        listColors.add("#BDEABF");
        listColors.add("#6159D8");
        listColors.add("#5C99E6");
        listColors.add("#1D3940");
        listColors.add("#E0375F");
        listColors.add("#E57647");
        listColors.add("#4AB238");
        listColors.add("#65E9B6");
        listColors.add("#A0E13A");
        listColors.add("#EBC787");
        listColors.add("#89AA5E");
        listColors.add("#D6BAE3");
        listColors.add("#DE65B5");
        listColors.add("#C28540");
        listColors.add("#8D8457");
        listColors.add("#A4998C");
        listColors.add("#9A46CC");
        listColors.add("#962D70");
        listColors.add("#7C50A9");
        listColors.add("#AF3539");
        listColors.add("#DF31AD");
        listColors.add("#C5DE65");
        listColors.add("#58BAA8");
        listColors.add("#693D65");
        listColors.add("#6B5558");
        listColors.add("#A63B5C");
        listColors.add("#471F16");
        listColors.add("#3F802C");
        listColors.add("#4F4518");
        listColors.add("#E2DDC0");
        listColors.add("#336B48");
        listColors.add("#AE4418");
        listColors.add("#A560A2");
        listColors.add("#6A7EEB");
        listColors.add("#2A2E6A");
        listColors.add("#DA6E97");
        listColors.add("#D79096");
        listColors.add("#595842");
        listColors.add("#DFB8C3");
        listColors.add("#AE7F6E");
        listColors.add("#8FBA8C");
        listColors.add("#97B9B1");
        listColors.add("#6F85BB");
        listColors.add("#2D6A85");
        listColors.add("#D336D7");
        listColors.add("#6FC6E9");
        listColors.add("#DF9CE3");
        listColors.add("#33282A");
        listColors.add("#C779E1");
        listColors.add("#655E9A");
        listColors.add("#7E4840");
        listColors.add("#61A5B3");
        listColors.add("#794911");
        listColors.add("#BDB486");
        listColors.add("#396062");
        listColors.add("#9F6A90");
        listColors.add("#595973");
        listColors.add("#756F22");
        listColors.add("#A75837");
        listColors.add("#D79B69");
        listColors.add("#7B878B");
        listColors.add("#B5841D");
        listColors.add("#A3B0DC");
        listColors.add("#648B69");
        listColors.add("#DCC168");

}
}

function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}
