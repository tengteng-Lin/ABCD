

function showPie(data,divID,title) {


    width = 350,
    height = 400;

    var svg = d3.select(divID)
        .append("svg")
        .attr({
            "width":width,
            "height":height
        })

    //legend尝试


    var legend = svg.append('defs')
        .append('g')
        .attr('id', 'graph')

    legend.append('line')
        .attr('x1', 0)
        .attr('y1', 0)
        .attr('x2', 15)
        .attr('y2', 0)
        .style('stroke', 'inherit')

    // legend.append('circle')
    //     .attr('cx', 15)
    //     .attr('cy', 0)
    //     .attr('r', 6.5)
    //     .attr('stroke', 'inherit')
    //     .attr('fill', '#000')

    let ele = svg.selectAll('graph-item').data(data);

    let ent = ele.enter().append('g')
        .attr('class', 'graph-item')

    ent.append('use')
        .attr('x', 160)
        .attr('y', (d,i) => i * 20+150)
        .attr('xlink:href', '#graph')
        .attr('stroke', "#000")
        .style('cursor', 'pointer')

    ent.append('text')
        .attr('x', (d,i) => i * 130 + 132)
        .attr('y', 20)
        .attr('dy', '.2em')
        .attr('fill', '#444')
        .style('font-size', '13px')
        .style('cursor', 'pointer')
        .text(d => d.name)
    // ent.render()


    // svg.append('text')
    //     .attr('x', width / 2 - 100)
    //     .attr('y', 30)
    //     .attr('class', 'title')
    //     .text(title);

    var main = svg.append("g")
        .attr("transform","translate(200, 200)")

    // var  = svg.append("g")
    //     .attr("transform","translate(200, 200)")
    // 设置标题
    // var svg = d3.select(divID).select("svg")



    // 转换原始数据为能用于绘图的数据
    var pie = d3.layout.pie()
        .sort(null)
        .value(function(d) {
            return d.value;
        });
    // pie是一个函数
    var pieData = pie(data);
    // 创建计算弧形路径的函数
    var radius = 100;
    var arc = d3.svg.arc()
        .innerRadius(0)
        .outerRadius(radius);

    var outerArc = d3.svg.arc()
        .innerRadius(0)
        .outerRadius(1.15 * radius);
    var oArc = d3.svg.arc()
        .innerRadius(1.1 * radius)
        .outerRadius(1.1 * radius);
    var slices = main.append('g').attr('class', 'slices');
    var lines = main.append('g').attr('class', 'lines');
    var labels = main.append('g').attr('class', 'labels');
    // 添加弧形元素（g中的path）
    var arcs = slices.selectAll('g')
        .data(pieData)
        .enter()

        .append('path')
        .attr('fill', function(d, i) {
            return getColor(i);
        })
        .attr('d', function(d){
            return arc(d);
        })
        .on("mouseover", function(d) {
            console.log(this)

            d3.select(this).transition().attr("d", function(d) {
                console.log(d);
                return outerArc(d);
            })
        })
        .on("mouseout", function(d){
            d3.select(this).transition().attr("d", function(d){
                return arc(d);
            })
        });

    // 添加文字标签
    var texts = labels.selectAll('text')
            .data(pieData)
            .enter()
            .append('text')
            .attr('dy', '0.35em')
            .attr('fill', function(d, i) {
                return getColor(i);
            })
            .text(function(d, i) {
                return d.data.name;
            })
            .style('text-anchor', function(d, i) {
                return midAngel(d)<Math.PI ? 'start' : 'end';
            })
            .attr('transform', function(d, i) {
                // // 找出外弧形的中心点
                // var pos = outerArc.centroid(d);
                // // 改变文字标识的x坐标
                // pos[0] = radius * (midAngel(d)<Math.PI ? 1.5 : -1.5);
                //
                // return 'translate(' + pos + ')';
                var x = arc.centroid(d)[0] * 2.4;
                var y = arc.centroid(d)[1] * 2.4;
                return "translate("+x+","+y+")";
            })
            .style('opacity', 1);








    // var polylines = lines.selectAll('polyline')
    //     .data(pieData)
    //     .enter()
    //     .append('polyline')
    //     .attr('points', function(d) {
    //         return [arc.centroid(d), arc.centroid(d), arc.centroid(d)];
    //     })
    //     .attr('points', function(d) {
    //         var pos = outerArc.centroid(d);
    //         pos[0] = radius * (midAngel(d)<Math.PI ? 1.5 : -1.5);
    //         return [oArc.centroid(d), outerArc.centroid(d), pos];
    //     })
    //     .style('opacity', 0.5);
}

function midAngel(d) {
    return d.startAngle + (d.endAngle - d.startAngle)/2;
}
function getColor(idx) {
    var palette = [
        '#2ec7c9', '#b6a2de', '#5ab1ef', '#ffb980', '#d87a80',
        '#8d98b3', '#e5cf0d', '#97b552', '#95706d', '#dc69aa',
        '#07a2a4', '#9a7fd1', '#588dd5', '#f5994e', '#c05050',
        '#59678c', '#c9ab00', '#7eb00a', '#6f5553', '#c14089'
    ]
    return palette[idx % palette.length];
}