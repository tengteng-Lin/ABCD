
function initData(svgEle) {
    var legendProperty = [{
        "name":"typed object",
        "color":"#CD9B1D"

    },{
        "name":"general node",
        "color":"#4682B4"

    },
        // {
        //     "name":"literal",
        //     "color":"#2F4F4F"
        //
        // },
    ]


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

        var legend = svgEle.append('defs')
            .append('g')
            .attr('id', 'graph_legend_oneStep')

        legend.append('line')
            .attr('x1', 0)
            .attr('y1', 0)
            .attr('x2', 15)
            .attr('y2', 0)
            .style('stroke', 'inherit')


        let ele = svgEle.selectAll('graph-item').data(legendProperty);

        let ent = ele.enter().append('g')
            .attr('class', 'graph-item')

        ent.append('use')
            .attr('x', (d,i) => i * 110 + 50)
            .attr('y', 20)
            .attr('xlink:href', '#graph_legend_oneStep')
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
    var arrowMarker1,arrowMarker2,arrowMarker1_type,arrowMarker2_type;

    //参数
    typeLineColor = "#abe2df"
    classCircleColor = "#CD9B1D"
    generalCircleColor = "#4682B4"

    function svgAddNode() {
        tables = uniqueArray(tables,"nsAndPre")
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

        // console.log(tables)
        // console.log(links)
        arrowMarker1 = g.append("marker")
            .attr("id", "arrowForOne")
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

                // if(d.relation==="rdf:type") return typeLineColor
                return '#000'
            });

        arrowMarker1_type = g.append("marker")
            .attr("id", "arrowForOne_type")
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
                // if(d.relation==="rdf:type") return typeLineColor
                return typeLineColor
            });

        arrowMarker2 = g.append("marker")
            .attr("id", "arrowForOne_1")
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
                // if(d.relation==="rdf:type") return typeLineColor
                return '#000'
            });

        arrowMarker2_type = g.append("marker")
            .attr("id", "arrowForOne_1_type")
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
                // if(d.relation==="rdf:type") return typeLineColor
                return '#000'
            });

        var linkGroup = {};  //用来分组，将两点之间的连线进行归类
        var linkMap = {};  //对连接线的计数
        for (var i = 0; i < links.length; i++) {
            var key = links[i].source.nsAndPre < links[i].target.nsAndPre ? links[i].source.nsAndPre + ':' + links[i].target.nsAndPre : links[i].target.nsAndPre + ':' + links[i].source.nsAndPre;
            if (!linkMap.hasOwnProperty(key)) {
                linkMap[key] = 0 ;
            }
            linkMap[key] += 1;
            if (!linkGroup.hasOwnProperty(key)) {
                linkGroup[key] = [];
            }
            linkGroup[key].push(links[i]);
        }
        //为每一条连接线分配size属性，同时对每一组连接线进行编号
        for (var i = 0; i < links.length; i++) {
            var key = links[i].source.nsAndPre < links[i].target.nsAndPre ? links[i].source.nsAndPre + ':' + links[i].target.nsAndPre : links[i].target.nsAndPre + ':' + links[i].source.nsAndPre;
            links[i].size = linkMap[key];
            //同一组的关系进行编号
            var group = linkGroup[key];
            var keyPair = key.split(':');
            var type = 'noself';
            if (keyPair[0] == keyPair[1]) {  //指向两个不同实体还是同一个实体
                type = 'self';
            }
            setLinkNumber(group, type); //给关系编号
        }

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
                if(d.relation==="rdf:type") return "url(#arrowForOne_type)";

                return "url(#arrowForOne)";
            }else return '';
        })
            .attr("marker-start",function (d,i){
                if(d.source.x >= d.target.x){
                    if(d.relation==="rdf:type") return "url(#arrowForOne_1_type)";


                    return "url(#arrowForOne_1)";
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
        if (d.linknum === 0) { //设置编号为0的连接线为直线，其他连接线会均分在两边
            d.x_start = x1;
            d.y_start = y1;
            d.x_end = x2;
            d.y_end = y2;
            return 'M' + x1 + ' ' + y1 + ' L ' + x2 + ' ' + y2;
        }
        var a = radius*d.linknum/3;
        var xm = d.target.x - d.source.x > 0 ? d.source.x + Math.sqrt((radius * radius - a * a) / (1 + tan * tan)) :
            d.source.x - Math.sqrt((radius * radius - a * a) / (1 + tan * tan));
        var ym = d.target.y - d.source.y > 0 ? d.source.y + Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan)) :
            d.source.y - Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan));
        var xn = d.target.x - d.source.x > 0 ? d.target.x - Math.sqrt((radius * radius - a * a) / (1 + tan * tan)) :
            d.target.x + Math.sqrt((radius * radius - a * a) / (1 + tan * tan));
        var yn = d.target.y - d.source.y > 0 ? d.target.y - Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan)) :
            d.target.y + Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan));
        if (d.target.x - d.source.x === 0 || tan === 0) {//斜率无穷大或为0时
            ym = d.target.y - d.source.y > 0 ? d.source.y + Math.sqrt(radius * radius - a * a) : d.source.y - Math.sqrt(radius * radius - a * a);
            yn = d.target.y - d.source.y > 0 ? d.target.y - Math.sqrt(radius * radius - a * a) : d.target.y + Math.sqrt(radius * radius - a * a);
        }

        var k = (x1 - x2) / (y2 - y1);//连线垂线的斜率
        var dx = Math.sqrt(a * a / (1 + k * k)); //相对垂点x轴距离
        var dy = Math.sqrt(a * a * k * k / (1 + k * k)); //相对垂点y轴距离
        if ((y2 - y1) === 0) {
            dx = 0;
            dy = Math.sqrt(a * a);
        }
        if (a > 0) {
            var xs = k > 0 ? xm - dx : xm + dx;
            var ys = ym - dy;
            var xt = k > 0 ? xn - dx : xn + dx;
            var yt = yn - dy;
        } else {
            var xs = k > 0 ? xm + dx : xm - dx;
            var ys = ym + dy;
            var xt = k > 0 ? xn + dx : xn - dx;
            var yt = yn + dy;
        }
        //记录连线起始和终止坐标，用于定位线上文字
        d.x_start = xs;
        d.y_start = ys;
        d.x_end = xt;
        d.y_end = yt;

        if(xs <xt) return 'M' + xs + ' ' + ys + ' L' + xt + ' ' + yt;
        else return 'M' + xt + ' ' + yt + ' L' + xs + ' ' + ys;
        // if(x1 < x2) return 'M' + x1 + ' ' + y1 + ' L ' + x2 + ' ' + y2;
        // else return 'M' + x2 + ' ' + y2 + ' L ' + x1 + ' ' + y1;
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
                return generalCircleColor
            })

        // addTablesData.append('text')
        //     .text(function (d) {
        //         return d.nsAndPre
        //     })
        //     .attr('text-anchor',"middle")
        //     .attr('font-size',12)

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
            .attr('font-size',12)
            .attr('fill','#000')
            .on("mouseover", function (d, i) {


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

    function setLinkNumber(group,type) {

        if(group.length==0) return;
        //对该分组内的关系按照方向进行分类，此处根据连接的实体ASCII值大小分成两部分
        var linksA = [], linksB = [];
        for(var i = 0 ;i < group.length; i++){
            var link = group[i];
            if(link.source.nsAndPre < link.target.nsAndPre){
                linksA.push(link);
            }else{
                linksB.push(link);
            }
        }
        // console.log(linksA,linksB)
        //确定关系最大编号。为了使得连接两个实体的关系曲线呈现对称，根据关系数量奇偶性进行平分。
        //特殊情况：当关系都是连接到同一个实体时，不平分
        var maxLinkNumber = 1;
        if(type==='self'){
            maxLinkNumber = group.length;
        }else{
            maxLinkNumber = group.length%2==0?group.length/2:(group.length+1)/2;
        }
        // console.log(maxLinkNumber)
        //如果两个方向的关系数量一样多，直接分别设置编号即可
        if(linksA.length===linksB.length){
            var startLinkNumber = 1;
            for(var i=0;i<linksA.length;i++){
                linksA[i].linknum = startLinkNumber++;
            }
            startLinkNumber = 1;
            for(var i=0;i<linksB.length;i++){
                linksB[i].linknum = startLinkNumber++;
            }
        }else{//当两个方向的关系数量不对等时，先对数量少的那组关系从最大编号值进行逆序编号，然后在对另一组数量多的关系从编号1一直编号到最大编号，再对剩余关系进行负编号
            //如果抛开负号，可以发现，最终所有关系的编号序列一定是对称的（对称是为了保证后续绘图时曲线的弯曲程度也是对称的）
            var biggerLinks,smallerLinks;
            if(linksA.length>linksB.length){
                biggerLinks = linksA;
                smallerLinks = linksB;
            }else{
                biggerLinks = linksB;
                smallerLinks = linksA;
            }

            var startLinkNumber = maxLinkNumber;
            for(var i=0;i<smallerLinks.length;i++){
                smallerLinks[i].linknum = startLinkNumber--;
            }
            var tmpNumber = startLinkNumber;

            startLinkNumber = 1;
            var p = 0;
            while(startLinkNumber<=maxLinkNumber){
                biggerLinks[p++].linknum = startLinkNumber++;
            }
            //开始负编号
            startLinkNumber = 0-tmpNumber;
            for(var i=p;i<biggerLinks.length;i++){
                biggerLinks[i].linknum = startLinkNumber++;
            }
        }


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

    function uniqueArray(array, key){
        var result = [array[0]];
        for(var i = 1; i < array.length; i++){
            var item = array[i];
            var repeat = false;
            for (var j = 0; j < result.length; j++) {
                if (item[key] == result[j][key]) {
                    repeat = true;
                    break;
                }
            }
            if (!repeat) {
                result.push(item);
            }
        }
        return result;
    }

    return {
        changePort,
        svgAddNode,
    }
}

function addNode(data) {
    console.log(data)
    flag =false;
    links.forEach(function (e){

        if(e.sourceNs === data.nsAndPre) {
            flag = true;
            return
        }
    })
    if(flag) return
    // let temp = tables.length
    // ajax发消息
    $.ajax({
        type:"get",
        url:"/addNode",
        data:{
            "session_id": sessionStorage.getItem("session_id"),
            "dataset_local_id": sessionStorage.getItem("dataset_local_id"),
            "subject": data.id,

        },
        contentType:"application/json; charset=utf-8",

        success:function (d){
            tmpp =d
            console.log(tmpp)



            for(var i=0;i<d[0].length;i++){
                    tables.push(d[0][i])
            }
            for(var i=0;i<d[1].length;i++){
                links.push(d[1][i])

            }
            initObj3.svgAddNode()

        },
        error:function (e){
            console.log(e)
        }
    })



}

