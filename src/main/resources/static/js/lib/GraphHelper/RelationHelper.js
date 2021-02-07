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

    let allTables, addTablesData, allLinks, addLinksData;

    function svgAddNode() {
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
        return 'M' + x1 + ' ' + y1 + ' L ' + x2 + ' ' + y2;
        // }


    }

    function forceInit(nodes, links) {//在nodes、links原对象上添加位置属性
        console.log(nodes)
        forceSimulation.nodes(nodes)
            .on('end', end)
            .on('tick', tick);
        forceSimulation.force('link')
            .links(links)
            .distance(function (d) {  //线的长短
                return 100
            })
    }

    function addTables() {
        addTablesData = g.selectAll('.tbClass').data(tables)
            .enter()
            .append('circle')
            .attr("r", 15)
            .attr("fill", '#FFF')
            .on('click', addNode)
            .attr('class', 'tbClass')

            .attr("transform", function (d) {
                return `translate(${d.x},${d.y})`
            })




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
            .attr("fill", '#000')
            .attr("marker-end", function (d, i) {
                console.log("i:"+i)
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
                    .attr("fill", "#000");

                return "url(#arrow" + i + ")";
            })
            .attr("stroke", '#000')
            .attr("stroke-width", 2)
        // .attr('class', 'link-polyline')

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

    tables.push({
        id: temp,
        uri:"add add add",
        nsAndPre:"add duan"
    })
    links.push({
        source: data.id,
        target: temp,
        relation: 'line1',

    })

    initObj.svgAddNode()

}


function toImg() {
    var serializer = new XMLSerializer();
    var source = '<?xml version="1.0" standalone="no"?>\r\n' + serializer.serializeToString(svg.node());
    var image = new Image;
    image.src = "data:image/svg+xml;charset=utf-8," + encodeURIComponent(source);
    var canvas = document.createElement("canvas");
    canvas.width = 1000;
    canvas.height = 800;
    var context = canvas.getContext("2d");
    context.fillStyle = '#fff';//#fff设置保存后的PNG 是白色的
    context.fillRect(0, 0, 10000, 10000);
    image.onload = function () {
        context.drawImage(image, 0, 0);
        var a = document.createElement("a");
        a.download = "name.png";
        a.href = canvas.toDataURL("image/png");
        a.click();
    };
}

function downloadSVG() {
    // console.log()
    let fileName = 'svg';
    let content = document.getElementById('wrap').innerHTML;
    let aTag = document.createElement('a');
    let blob = new Blob([content]);
    aTag.download = fileName;
    aTag.href = URL.createObjectURL(blob);
    aTag.click();
    URL.revokeObjectURL(blob);
}