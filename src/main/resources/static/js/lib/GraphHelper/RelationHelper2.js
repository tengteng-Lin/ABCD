function showRelation(tables,links,divID) {
    let width = 1200
    let height = 800
    let radius = 15


    var lineColor_0 = "#CD9B1D"; //连线颜色
    var lineColor_1 = "#2F4F4F"
    var lineColor_2 ="#4682B4"

    let svgEle = d3.select(divID)
        .append("svg")
        .attr('width', width)
        .attr('height', height)

    //初始化
    let g, gPosition;
    //初始化力导向布局
    let forceSimulation = d3.forceSimulation()
        .force('charge', d3.forceManyBody().strength(-100))//电荷力
        .force('forceCollide', d3.forceCollide().radius(50))//检测碰撞
        .force('link', d3.forceLink().id(function (d) {
            return d.id
        }))//link

    let zoom = d3.zoom()
        .scaleExtent([0.1, 10])
        .on("zoom", function (d) {
            g.attr("transform", function () {//初始化
                return `translate(${d3.event.transform.x + gPosition.x},${d3.event.transform.y + gPosition.y}) scale(${d3.event.transform.k})`
            })
        })
    svgEle.call(zoom).on('dblclick.zoom', null);
    g = svgEle.append('g')
    g.append("g").attr('class', 'links')

    //处理数据
    links.forEach(function(e){
        let sourceNode = tables.filter(function(n){

            if((n.nsAndPre !== undefined) && (n.nsAndPre !== null) && (n.nsAndPre !== '')){
                return n.nsAndPre === e.sourceNs;
            }

        })[0];

        let targetNode = tables.filter(function(n){
            if((n.nsAndPre !== undefined) && (n.nsAndPre !== null) && (n.nsAndPre !== '')){
                return n.nsAndPre === e.targetNs;
            }

        })[0];

        // console.log(sourceNode)
        e.source = sourceNode;
        e.target = targetNode;

    });

    console.log(tables)
    console.log(links)

    forceSimulation.nodes(tables)
        .on('end', end)
        .on('tick', tick);
    forceSimulation.force('link')
        .links(links)
        .distance(function (d) {  //线的长短
            return 200
        })

    addTablesData = g.selectAll('.tbClass').data(tables)
        .enter()
        .append('g')
        .on('click', addNode)
        .attr("cursor","pointer")
        .attr('class', 'tbClass')
        .attr("transform", function (d) {
            return `translate(${d.x},${d.y})`
        })
        .on("mouseover", function (d, i) {
            console.log(d)

            d3.select(".tooltip")
                .text(d.uri)
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20 ) + "px")
                .style("display", "block")
                .style("visibility", "visible");

        })
        .on("mouseout", function (d, i) {

            d3.select(".tooltip")
                .style("visibility", "hidden");

        })

    addTablesData.append('circle')
        .attr("r",15)
        .attr("fill", function (d){
            if(d.type===1) return classCircleColor
            return '#FFF'
        })

    addTablesData.append('text')
        .text(function (d) {
            return d.nsAndPre
        })
        .attr('text-anchor',"middle")
        .attr('font-size',12)

    allTables = g.selectAll('.tbClass').data(tables)

    function tick(){
        allTables.attr("transform", function (d) {
            return `translate(${d.x},${d.y})`
        })
        allLinks.attr('d', test)

        addLinksData.attr("marker-end", function (d, i) {

            if(d.source.x < d.target.x){
                var arrowMarker = g.append("marker")
                    .attr("id", "arrow" + i)
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("markerWidth", "16")
                    .attr("markerHeight", "15")
                    .attr("viewBox", "0 0 12 12")
                    .attr("refX", 10)
                    .attr("refY", 6)
                    .attr("orient", "auto")
                    .append("path")
                    .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                    .attr("fill",  function (){
                        if(d.relation==="rdf:type") return typeLineColor
                        return '#000'
                    });

                return "url(#arrow" + i + ")";
            }else return '';
        })
            .attr("marker-start",function (d,i){
                if(d.source.x >= d.target.x){
                    var arrowMarker = g.append("marker")
                        .attr("id", "arrow1" + i)
                        .attr("markerUnits", "userSpaceOnUse")
                        .attr("markerWidth", "16")
                        .attr("markerHeight", "15")
                        .attr("viewBox", "0 0 12 12")
                        .attr("refX", 3)
                        .attr("refY", 6)
                        .attr("orient", "auto")
                        .append("path")
                        .attr("d", "M10,2 L2,6 L10,10 L6,6 L10,2")
                        .attr("fill",  function (){
                            if(d.relation==="rdf:type") return typeLineColor
                            return '#000'
                        });

                    return "url(#arrow1" + i + ")";
                }else return ''
            })
    }

    function end(){

    }






    console.log(links)
    addLinksData = d3.select('.links')
        .selectAll("path")
        .data(links)
        .enter()
        .append("path")
        .attr("cursor","pointer")
        .attr("fill", function (d){
            if(d.relation==="rdf:type") return typeLineColor
            return '#000'
        })
        .attr("stroke",  function (d){
            if(d.relation==="rdf:type") return typeLineColor
            return '#000'
        })
        .attr("stroke-width", 2)
        .attr('id',function(d,i) {return 'edgepath'+i})
        .on("mouseover", function (d, i) {
            console.log(d)

            d3.select(".tooltip")
                .text(d.uri)
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20 ) + "px")
                .style("display", "block")
                .style("visibility", "visible");

        })
        .on("mouseout", function (d, i) {

            d3.select(".tooltip")
                .style("visibility", "hidden");

        });
    // .attr('class', 'link-polyline')


    edgelabels = g.selectAll(".edgelabel")
        .data(links)
        .enter()
        .append('text')
        .attr("cursor","pointer")
        // .style("pointer-events", "none")
        .attr('class','edgelabel')
        .attr('id',function(d,i){return 'edgelabel'+i})
        // .attr('text-anchor',"middle")
        .attr('dx',60)
        .attr('dy',-2)
        .attr('font-size',15)
        .attr('fill','#000')
        .on("mouseover", function (d, i) {
            console.log(d)

            d3.select(".tooltip")
                .text(d.uri)
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20 ) + "px")
                .style("display", "block")
                .style("visibility", "visible");

        })
        .on("mouseout", function (d, i) {

            d3.select(".tooltip")
                .style("visibility", "hidden");

        });

    edgelabels.append('textPath')
        .attr('xlink:href',function(d,i) {return '#edgepath'+i})
        .attr("cursor","pointer")
        // .style("pointer-events", "none")
        .text(function(d,i){return d.relation})



    allLinks = d3.select('.links')
        .selectAll('path')
        .data(links)

    function startFn(d) {//拖动开始
        if (!d3.event.active) {
            forceSimulation.alphaTarget(0.8).restart()//[0,1]
        }
        d.fx = d.x
        d.fy = d.y
    }

    function dragFn(d) {//拖动中
        d.fx = d3.event.x
        d.fy = d3.event.y
    }

    function endFn(d) {//拖动结束
        if (!d3.event.active) {
            forceSimulation.alphaTarget(0)
        }
        // d.fx = null
        // d.fy = null
    }


}