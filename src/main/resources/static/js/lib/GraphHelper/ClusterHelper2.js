
function showCluster2(data,data1,property,divID) {
    numID  = parseInt(divID.substring(3));



    var lineColor_0 = "#CD9B1D"; //连线颜色
    var lineColor_1 = "#2F4F4F"
    var lineColor_2 = "#CD5C5C"

    // 树状图因为默认是上往下渲染的，改成从左往右渲染后会发现width和height都倒过来了，可以看具体参数的细节
    let width = 300
    let height = 300

    var cluster = d3.layout.cluster()
        .size([height, width-200]);

    var diagonal = d3.svg.diagonal()
        .projection(function(d) {
            console.log(d)
            return [d.y, d.x];
        });

    var svg = d3.select("#"+divID).append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")

        .attr("transform", "rotate(180,180,120)translate(150,10)scale(0.7)");

    var svg2 = d3.select("#"+divID).append("svg")
        .attr("width", width)
        .attr("height", height)
        // .attr("transform","translate(20,0)")
        .append("g")

        .attr("transform", "translate(80,20)scale(0.7)");

    var nodes = cluster.nodes(data);
    var links = cluster.links(nodes);
    for(i=0;i<links.length;i++){
        if(links[i].target.inOrOut===0){
            console.log("swap……")
            c=links[i].source
            links[i].source = links[i].target
            links[i].target = c
            // console.log("before:")
            // swap(links[i])
            // console.log("after:")
        }
    }
    console.log(links)

    var nodes2 = cluster.nodes(data1);
    var links2 = cluster.links(nodes2)
    for(i=0;i<links2.length;i++){
        if(links2[i].target.inOrOut===0){
            console.log("swap……")
            c=links2[i].source
            links2[i].source = links2[i].target
            links2[i].target = c
            // console.log("before:")
            // swap(links[i])
            // console.log("after:")
        }
    }
    console.log(links2)

    var link = svg.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-end", function (dd, i) {

            var arrowMarker = svg.append("marker")

                .attr("id", "arrow1" +numID + i)
                .attr("markerUnits", "userSpaceOnUse")
                .attr("markerWidth", "16")
                .attr("markerHeight", "15")
                .attr("viewBox", "0 0 12 12")
                .attr("refX", function () {
                    if(dd.source.inOrOut===0){
                        return 18;
                    }else{
                        return 9;
                    }
                })
                .attr("refY", 6)
                .attr("orient", function () {
                    return "auto"
                    // if(dd.target.inOrOut===0){
                    //     return "180";
                    // }else{
                    //     return "auto";
                    // }
                })
                .append("svg:path")
                .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                // .attr("d", "M2,2 L10,6 L2,10 L"+dd.y+","+dd.x+" L2,2")
                .attr("fill", function () {
                    if (dd.target.type===0 || dd.source.type===0){
                        if(dd.target.inOrOut===0 || dd.source.inOrOut===0){
                            return lineColor_2
                        }else {
                            return lineColor_0
                        }
                    }else{
                        return lineColor_1
                    }
                });

            return "url(#arrow1" + numID + i + ")";
        })
        .attr("stroke",function (d){
            if(d.target.type===0){
                if(d.target.inOrOut===0){
                    return lineColor_2
                }else{
                    return lineColor_0
                }
            }else{
                return lineColor_1
            }
        })
        .attr("d", diagonal);

    var link2 = svg2.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links2)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-end", function (dd, i) {
            var arrowMarker = svg.append("marker")

                .attr("id", "arrow2" + numID + i)
                .attr("markerUnits", "userSpaceOnUse")
                .attr("markerWidth", "16")
                .attr("markerHeight", "15")
                .attr("viewBox", "0 0 12 12")
                .attr("refX", function () {
                    if(dd.source.inOrOut===0){
                        return 18;
                    }else{
                        return 9;
                    }
                })
                .attr("refY", 6)
                .attr("orient", function () {
                    return "auto"
                    // if(dd.target.inOrOut===0){
                    //     return "180";
                    // }else{
                    //     return "auto";
                    // }
                })
                .append("svg:path")
                .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                .attr("fill", function () {
                    if (dd.target.type===0 || dd.source.type===0){
                        if(dd.target.inOrOut===0 || dd.source.inOrOut===0){
                            return lineColor_2
                        }else{
                            return lineColor_0
                        }
                    }else{
                        return lineColor_1
                    }
                })


            return "url(#arrow2" + numID +i + ")";
        })
        .attr("stroke",function (d){
            if(d.target.type===0 || d.source.type===0){

                if(d.source.inOrOut===0){
                    return lineColor_2
                }else if(d.target.inOrOut===1 || d.source.inOrOut===1){
                    return lineColor_0
                }
            }else{

                return lineColor_1
            }

        })
        .attr("d", diagonal);



    var node = svg.selectAll(".node")
        .data(nodes)
        .enter()
        .append("g")
        .attr("class", function (d,i) {
            // console.log(d)
            // console.log(i)
            if(i===0){
                return "root"
            }
            else return "node"
        })
        
        .attr("transform", function(d) {
            return "translate(" + d.y + "," + d.x + ")";
        })

    var node2 = svg2.selectAll(".node")
        .data(nodes2)
        .enter()
        .append("g")
        .attr("class", function (d,i) {
            // console.log(d)

            if(i===0){
                return "root"
            }
            else return "node"
        })

        .attr("transform", function(d) {
            return "translate(" + d.y + "," + d.x + ")";
        })




    // var circle = svg
    //     .append("circle")
    //     .attr("r",width/2)
    //     .attr("cx",width/5)
    //     .attr("cy",height/2)
    //     .attr("fill","none")
    //     .attr("stroke",lineColor_1)
    //
    // var circle2 = svg2
    //     .append("circle")
    //     .attr("r",width/2)
    //     .attr("cx",width/5)
    //     .attr("cy",height/2)
    //     .attr("fill","none")
    //     .attr("stroke",lineColor_1)

    var root = svg.selectAll(".root")
        .append("circle")
        .attr("r", 8.5);

    var root2 = svg2.selectAll(".root")
        .append("circle")
        .attr("r", 8.5);



    node.append("text")
        .attr("dx", function(d) {
            return d.children ? 90 : 8;
        })
        .attr("dy", function (d) {
            return d.children ? 25:3
        })
        .attr("transform", "rotate(180,18,120)translate(0,240)")
        // .attr('transform', function (d) { return d.x < 180 ? null : 'rotate(270)' })
        .style("text-anchor", function(d) {
            return d.children ? "end" : "end";
        })
        .attr("font-size",16)
        .text(function(d) {
            return d.children ? "" : d.name;
        });

    node2.append("text")
        .attr("dx", function(d) {
            return d.children ? 18 : 8;
        })
        .attr("dy", function (d) {
            if(! d.children){
                return 3
            }else{
                return 25
            }
        })
        .attr("font-size",16)
        .style("text-anchor", function(d) {
            return d.children ? "end" : "start";
        })
        .text(function(d) {
            return d.children ? "" : d.name;
        });


    var line1 = svg
        .append("line")
        .data(root)
        .attr("x1", function (d) {

        return d[0].cx;
        })
        .attr("y1", function (d) {
            return 150;
        })
        .attr("x2", function (d) {
            return -width
        })
        .attr("y2", function (d) {
            return 150
        })
        .attr("stroke", "black")
        .attr("stroke-width", "2px");

    var defs = svg2.append("defs");
    var arrowMarker = defs.append("marker")
        .attr("id","arrowProperty")
        .attr("markerUnits","strokeWidth")
        .attr("markerWidth","120")
        .attr("markerHeight","120")
        .attr("viewBox","0 0 120 120")
        .attr("refX","6")
        .attr("refY","6")
        .attr("orient","auto");

    var arrow_path = "M2,2 L10,6 L2,10 L6,6 L2,2";

    arrowMarker.append("path")
        .attr("d",arrow_path)
        .attr("fill","#000");

    svg2 .append("line")
        .data(root2)
        .attr("x1", function (d) {

            return -160;
        })
        .attr("y1", function (d) {
            return 150;
        })
        .attr("x2", function (d) {
            return 0
        })
        .attr("y2", function (d) {
            return 150
        })
        .attr("stroke", "black")
        .attr("stroke-width", "2px")
        .attr("marker-end","url(#arrowProperty)");

    property1 = property.substring(0,property.length/2)
    property2 = property.substring(property1.length)


    svg.append("text")
         .data(root)
        .attr("dx", 435)
        .style("text-anchor", "start")
        .attr("dy", 90)
        // .attr("font-size",20)
        .attr("transform", "rotate(180,180,120)")
        .attr("textLength",50)
        .attr("lengthAdjust","spacingAndGlyphs")
        .attr("font-style","italic")
        .attr("font-weight","bold")
        .attr("font-family","Georgia, serif")
        .text(property1)

    svg2.append("text")
        .data(root)
        .attr("dx", -115)
        .style("text-anchor", "start")
        .attr("dy", 150)
        // .attr("font-size",20)
        // .attr("transform", "rotate(180,180,120)translate(65,-70)")
        .attr("textLength",50)
        .attr("lengthAdjust","spacingAndGlyphs")
        .attr("font-style","italic")
        .attr("font-weight","bold")
        .attr("font-family","Georgia, serif")
        .text(property2)




}