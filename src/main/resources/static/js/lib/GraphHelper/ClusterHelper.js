
function showCluster(data,divID) {

    //定义legend
    legendProperty = [{
        "name":"out property",
        "color":"#CD9B1D"

    },{
        "name":"in property",
        "color":"#CD5C5C"

    },
        {
            "name":"class",
            "color":"#2F4F4F"

        },
    ]

    // numID = parseInt(divID.substring(6));

    var lineColor_0 = "#CD9B1D"; //连线颜色
    var lineColor_1 = "#2F4F4F"
    var lineColor_2 = "#CD5C5C"


    let width = 400
    let height = 300

    var cluster = d3.layout.cluster()
        .size([height, width-200]);

    var diagonal = d3.svg.diagonal()
        .projection(function(d) {
            return [d.y, d.x];
        });

    var main = d3.select("#"+divID).append("svg")
        .attr("width", width)
        .attr("height", height)


    var legend = main.append('defs')
        .append('g')
        .attr('id', 'graph')

    legend.append('line')
        .attr('x1', 0)
        .attr('y1', 0)
        .attr('x2', 15)
        .attr('y2', 0)
        .style('stroke', 'inherit')


    let ele = main.selectAll('graph-item').data(legendProperty);

    let ent = ele.enter().append('g')
        .attr('class', 'graph-item')

    ent.append('use')
        .attr('x', (d,i) => i * 110 + 50)
        .attr('y', 20)
        .attr('xlink:href', '#graph')
        .attr('stroke', d => d.color)
        .style('cursor', 'pointer')

    ent.append('text')
        .attr('x', (d,i) => i * 110 + 70)
        .attr('y', 20)
        .attr('dy', '.2em')
        .attr('fill', '#444')
        .style('font-size', '13px')
        .style('cursor', 'pointer')
        .text(d => d.name)

    var svg = main.append("g")
        .attr("transform", "translate(120,0)scale(0.7)");

    var nodes = cluster.nodes(data);
    var links = cluster.links(nodes);

    console.log(nodes)
    console.log(links)

    for(i=0;i<links.length;i++){
        if(links[i].target.inOrOut===0){
            console.log("swap……")
            c=links[i].source
            links[i].source = links[i].target
            links[i].target = c

        }
    }
    console.log(links)



    var link = svg.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-end", function (dd, i) {

            var arrowMarker = svg.append("marker")

                .attr("id", "arrow" +  i)
                .attr("markerUnits", "userSpaceOnUse")
                .attr("markerWidth", "16")
                .attr("markerHeight", "15")
                .attr("viewBox", "0 0 12 12")
                .attr("refX", function () {
                    // return 9

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
                });

            return "url(#arrow" +  i + ")";
        })

        .attr("stroke",function (d){


            if(d.target.type===0 || d.source.type===0){

                if(d.target.inOrOut===0 || d.source.inOrOut===0){
                    return lineColor_2
                }else{
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
            if(i===0){
                return "root"
            }
            else return "node"
        })
        
        .attr("transform", function(d) {
            return "translate(" + d.y + "," + d.x + ")";
        })

    var root = svg.selectAll(".root")
        .append("circle")
        .attr("r", 8.5);



    node.append("text")
        .attr("dx", function(d) {
            return d.children ? -8 : 8;
        })
        .attr("dy", 3)
        .style("text-anchor", function(d) {
            return d.children ? "end" : "start";
        })
        .text(function(d) {
            console.log(d)

            // return d.children ? "" : d.name;
            return d.name;
        });



}