var stage
function initVisualization(){
    stage = new Kinetic.Stage({
        container: 'container',
        width: 578,
        height: 200
    });

    var layer = new Kinetic.Layer();

    var rect = new Kinetic.Rect({
        x: 239,
        y: 75,
        width: 100,
        height: 50,
        fill: 'green',
        stroke: 'black',
        strokeWidth: 4,
        name:'worldcat'
    });

    // add the shape to the layer
    layer.add(rect);

    // add the layer to the stage
    stage.add(layer);
}

function updateVisualization(){
    var count = document.getElementById('formID:counter').valueOf();
    console.log("value of count is: "+count);
    var worldcat = stage.get('.worldcat');
    worldcat.apply('transitionTo', {
        scale: {
            x: Math.random() * 2,
            y: Math.random() * 2
        },
        duration: 1,
        easing: 'elastic-ease-out'
    });
}