function initData(svgEle) {


    //初始化
    let g, gPosition;
    //初始化力导向布局
    let forceSimulation = d3.forceSimulation()
        .force('charge', d3.forceManyBody().strength(-100))//电荷力
        .force('forceCollide', d3.forceCollide().radius(50))//检测碰撞
        .force('link', d3.forceLink().id(function (d) {
            return d.id
        }))//link

    initSVG()

    //首次执行
    function initSVG() {
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
    }

    //初始化位置
    function changePort() {
        gPosition = svgEle.select('g').node().getBBox()
        gPosition.x = -gPosition.x + 20
        gPosition.y = -gPosition.y + 20

        g.transition().delay(0).attr('transform', function () {
            return `translate(${gPosition.x},${gPosition.y}) scale(1)`
        })
    }


    //绘制

    let allTables, addTablesData, allLinks, addLinksData,edgelabels;

    //参数
    typeLineColor = "#abe2df"
    classCircleColor = "#f2a"

    function svgAddNode() {
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

        forceInit(tables, links)
        addTables()
        addLinks()
        initDrag()
    }

    function tick() {//force 迭代执行函数
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

    function end() {//force end函数

    }

    function test(d) {
        // console.log(d)
        var tan = Math.abs((d.target.y - d.source.y) / (d.target.x - d.source
            .x)); //圆心连线tan值
        var x1 = d.target.x - d.source.x > 0 ? Math.sqrt(radius * radius / (
            tan * tan + 1)) + d.source.x :
            d.source.x - Math.sqrt(radius * radius / (tan * tan +
            1)); //起点x坐标
        var y1 = d.target.y - d.source.y > 0 ? Math.sqrt(radius * radius *
            tan * tan / (tan * tan + 1)) + d.source.y :
            d.source.y - Math.sqrt(radius * radius * tan * tan / (tan *
            tan + 1)); //起点y坐标
        var x2 = d.target.x - d.source.x > 0 ? d.target.x - Math.sqrt(radius * radius / (1 + tan * tan)) :
            d.target.x + Math.sqrt(radius * radius / (1 + tan *
            tan)); //终点x坐标
        var y2 = d.target.y - d.source.y > 0 ? d.target.y - Math.sqrt(radius * radius * tan * tan / (1 + tan * tan)) :
            d.target.y + Math.sqrt(radius * radius * tan * tan / (1 + tan *
            tan)); //终点y坐标
        if (d.target.x - d.source.x === 0 || tan === 0) { //斜率无穷大的情况或为0时
            y1 = d.target.y - d.source.y > 0 ? d.source.y + radius : d.source.y - radius;
            y2 = d.target.y - d.source.y > 0 ? d.target.y - radius : d.target.y + radius;
        }
        // 防报错
        if (!x1 || !y1 || !x2 || !y2) {
            return
        }
        // if (d.linknum == 0) { //设置编号为0的连接线为直线，其他连接线会均分在两边
        // d.x_start = x1;
        // d.y_start = y1;
        // d.x_end = x2;
        // d.y_end = y2;
        if(x1 < x2) return 'M' + x1 + ' ' + y1 + ' L ' + x2 + ' ' + y2;
        else return 'M' + x2 + ' ' + y2 + ' L ' + x1 + ' ' + y1;
        // }


    }

    function forceInit(nodes, links) {//在nodes、links原对象上添加位置属性


        forceSimulation.nodes(nodes)
            .on('end', end)
            .on('tick', tick);


        forceSimulation.force('link')
            .links(links)
            .distance(function (d) {  //线的长短
                return 200
            })
    }

    function addTables() {
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
    }

    function initDrag() {
        allTables.call(d3.drag()//添加拖动事件
            .on('start', startFn)
            .on('drag', dragFn)
            .on('end', endFn)
        )
    }

    //左右连线计算
    function linkFn(d) {
        let res = []
        if (d.source.x < d.target.x) {
            res[2] = [d.target.x, d.target.y]
            res[3] = [d.target.x, d.target.y]
            res[0] = [d.source.x, d.source.y]
            res[1] = [d.source.x, d.source.y]
        } else {
            res[1] = [d.source.x, d.source.y]
            res[0] = [d.source.x, d.source.y]
            res[3] = [d.target.x, d.target.y]
            res[2] = [d.target.x, d.target.y]

        }

        return res.map(v => v.join(',')).join(' ')
    }

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

    //添加连线
    function addLinks() {
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
    }


    //节点高亮
    function colsLight(data, isEnter) {
        lineLight(data, isEnter)
        d3.select(this)
            .select('rect')
            .transition()
            // .delay(50)
            .attr('fill', isEnter ? colsStyle.fillLight : colsStyle.fillDefalt)

    }

    //线高亮
    function lineLight(data, isEnter) {
        d3.selectAll('.link-polyline')
            .attr('stroke', function (d) {
                let source = d.source.cols[d.sourceIndex - 1]
                let target = d.target.cols[d.targetIndex - 1]
                if (data.id === source.id || data.id === target.id) {
                    return isEnter ? lineStyle.strokeLight : lineStyle.strokeDefault
                } else {
                    return lineStyle.strokeDefault
                }
            })
            .attr('stroke-width', function (d) {
                let source = d.source.cols[d.sourceIndex - 1]
                let target = d.target.cols[d.targetIndex - 1]
                if (data.id === source.id || data.id === target.id) {
                    return isEnter ? lineStyle.strokeWidthLight : lineStyle.strokeWidthDefault
                } else {
                    return lineStyle.strokeWidthDefault
                }
            })
    }

    return {
        changePort,
        svgAddNode,
    }
}

function addNode(data) {
    let temp = tables.length
    //ajax发消息
    // $.ajax({
    //     type:"get",
    //     url:"/search",
    //     data:data,
    //     contentType:"application/json; charset=utf-8",
    //
    //     success:function (d){
    //         updateResult(d)
    //
    //     },
    //     error:function (e){
    //         console.log(e)
    //     }
    // })

    tables.push({
        nsAndPre:"add duan",
        uri:"add add add",
        id: temp,
        type:1 // 因为没法直接判断是不是type，所以后台传过来的时候设置

    })
    links.push({
        sourceNs: "add duan",
        targetNs: "node0:aaaa",
        relation: "rdf:type",
        uri:"xxxxxxxxx"

    })



    initObj.svgAddNode()

}
