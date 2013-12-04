var stage;
var barsLayer = new Kinetic.Layer();
var worldcatLayer = new Kinetic.Layer();
var arxivLayer = new Kinetic.Layer();
var mendeleyLayer = new Kinetic.Layer();
var nytLayer = new Kinetic.Layer();
var vizInitiated;



stage = new Kinetic.Stage({
    container: 'container',
    width: 1100,
    height: 500
});





var arxivRect = new Kinetic.Rect({
    x: 10,
    y: stage.getHeight() - 115 - 10,
    width: 245,
    height: 10,
    fill: 'green',
    //        stroke: 'black',
    //        strokeWidth: 0,
    name: 'arxivBar'
});



var mendeleyRect = new Kinetic.Rect({
    x: 10 + 245 + 10,
    y: stage.getHeight() - 115 - 10,
    width: 245,
    height: 10,
    fill: 'green',
    //        stroke: 'black',
    //        strokeWidth: 0,
    name: 'mendeleyBar'
});

var worldcatRect = new Kinetic.Rect({
    x: 10 + 245 + 10 + 245 + 10,
    y: stage.getHeight() - 115 - 10,
    width: 245,
    height: 10,
    fill: 'green',
    //        stroke: 'black',
    //        strokeWidth: 0,
    name: 'worldcatBar'
});

var nytRect = new Kinetic.Rect({
    x: 10 + 245 + 10 + 245 + 10 + 245 + 10,
    y: stage.getHeight() - 115 - 10,
    width: 245,
    height: 10,
    fill: 'green',
    //        stroke: 'black',
    //        strokeWidth: 0,
    name: 'nytBar'
});


barsLayer.add(arxivRect);
barsLayer.add(mendeleyRect);
barsLayer.add(worldcatRect);
barsLayer.add(nytRect);
stage.add(barsLayer);


var arxivPic = new Image();
arxivPic.onload = function() {
    var arxivPicOriginal = new Kinetic.Image({
        x: 10,
        y: stage.getHeight() - 5 - 115,
        image: arxivPic,
        width: 245,
        height: 115

    });
    arxivLayer.add(arxivPicOriginal);
    stage.add(arxivLayer);

};
arxivPic.src = 'resources/img/arxiv.jpg';


var mendeleyPic = new Image();
mendeleyPic.onload = function() {
    var mendeleyPicOriginal = new Kinetic.Image({
        x: 10 + 245 + 10,
        y: stage.getHeight() - 5 - 115,
        image: mendeleyPic,
        width: 245,
        height: 115

    });
    mendeleyLayer.add(mendeleyPicOriginal);
    stage.add(mendeleyLayer);
};
mendeleyPic.src = 'resources/img/mendeley.jpg';

var worldcatPic = new Image();
worldcatPic.onload = function() {
    var worldcatPicOriginal = new Kinetic.Image({
        x: 10 + 245 + 10 + 245 + 10,
        y: stage.getHeight() - 5 - 115,
        image: worldcatPic,
        width: 245,
        height: 115

    });
    worldcatLayer.add(worldcatPicOriginal);
    stage.add(worldcatLayer);
    var worldcatz = new Image();

    worldcatPicOriginal.on("mouseover", function() {
        worldcatPicOriginal.setImage(worldcatz);
        worldcatLayer.draw();
    });
    worldcatPicOriginal.on("mouseout", function() {
        worldcatPicOriginal.setImage(worldcatPic);
        worldcatLayer.draw();
    });
    worldcatz.src = 'resources/img/worldcatz.jpg';

}
worldcatPic.src = 'resources/img/worldcat.jpg';


var nytPic = new Image();
nytPic.onload = function() {
    var nytPicOriginal = new Kinetic.Image({
        x: 10 + 245 + 10 + 245 + 10 + 245 + 10,
        y: stage.getHeight() - 5 - 115,
        image: nytPic,
        width: 245,
        height: 115

    });
    nytLayer.add(nytPicOriginal);
    stage.add(nytLayer);
};
nytPic.src = 'resources/img/nyt.jpg';


function initVisualization() {
}



function updateVisualization() {
//    if (vizInitiated != true) {
//        initVisualization();
//        vizInitiated = true;
//    }
//    var progressMsg = document.getElementById('formID:counter').value;
    var progressMsg = $('#counter').val();


    console.log("value of progressMsg is: " + progressMsg);

    var worldcat;
    if (progressMsg.indexOf("worldcat in progress") != -1) {
        worldcat = worldcatRect;
        var tween = new Kinetic.Tween({
            node: worldcat,
            duration: 2,
//            y: -100,
            scaleY: -10,
            easing: Kinetic.Easings.ElasticEaseOut
        });
        tween.play();
    }
    if (progressMsg.indexOf("worldcat returned") != -1) {
        worldcat = worldcatRect;
        var tween = new Kinetic.Tween({
            node: worldcat,
            duration: 2,
//            y: -200,
            scaleY: -20,
            easing: Kinetic.Easings.ElasticEaseOut
        });
        tween.play();
    }
    if (progressMsg.indexOf("mendeley returned") != -1) {
        var mendeley = mendeleyRect;
        var tween = new Kinetic.Tween({
            node: mendeley,
            duration: 2,
//            y: -200,
            scaleY: -20,
            easing: Kinetic.Easings.ElasticEaseOut
        });
        tween.play();
    }

    if (progressMsg.indexOf("arxiv returned") != -1) {
        var arxiv = arxivRect;
        var tween = new Kinetic.Tween({
            node: arxiv,
            duration: 2,
//            y: -200,
            scaleY: -20,
            easing: Kinetic.Easings.ElasticEaseOut
        });
        tween.play();
    }

    if (progressMsg.indexOf("nyt returned") != -1) {
        var nyt = nytRect;
        var tween = new Kinetic.Tween({
            node: nyt,
            duration: 2,
//            y: -200,
            scaleY: -20,
            easing: Kinetic.Easings.ElasticEaseOut
        });
        tween.play();
    }

}

$(document).ready(function() {
    initVisualization();
});