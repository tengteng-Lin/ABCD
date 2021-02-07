
function showScatter(dataset,title,divID) {
    var svgWidth = 500;
    var svgHight = 280;
    var padding =  50;

    // 数据点
    // var dataset = [
    //     [0, 0], [65.66, 420],[120, 56],[130, 623], [134, 352], [200, 200],[333, 666], [360, 320],  [520, 260],
    //     [652, 52],  [729, 656],[777, 888],
    //     [905, 177],  [1200, 1000]
    // ];

    // 创建SVG
    var svg = d3.select(divID)
        .append('svg')
        .attr('width', svgWidth)
        .attr('height', svgHight);

    // 设置标题
    svg.append('text')
        .attr('x', svgWidth / 2 - 90)
        .attr('y', 30)
        .attr('class', 'title')
        .text(title);

    // 创建比例尺
    var xScale = d3.scale.linear()
        .domain([0, d3.max(dataset, function(d) {
            return d.degreeCount;
        })]).range([padding, svgWidth - padding * 2]);

    var yScale = d3.scale.linear()
        .domain([0, d3.max(dataset, function(d) {
            return d.resCount;
        })]).range([svgHight - padding, padding]);

    var rScale = d3.scale.linear()
        .domain([0, d3.max(dataset, function(d) {
            return d.resCount;
        })]).range([2, 4]);

    // 设置散点的坐标, 半径
    svg.selectAll('circle')
        .data(dataset)
        .enter()
        .append('circle')
        .attr('cx', function(d) {
            return xScale(d.degreeCount);
        })
        .attr('cy', function(d) {
            return yScale(d.resCount);
        })
        .attr('r', function(d) {
            return rScale(d.resCount);
        });

    // 设置文本   【可以设置为tooltip】
    // svg.selectAll('text')
    //     .data(dataset)
    //     .enter()
    //     .append('text')
    //     .text(function(d) {
    //         return '(' + d.degreeCount + ', ' + d.resCount+ ')';
    //     })
    //     .attr('x', function(d) {
    //
    //         // 设置偏移量，让文本位于上方  //设置为tooltip
    //         return xScale(d.degreeCount) - 20;
    //     })
    //     .attr('y', function(d) {
    //         return yScale(d.resCount) - 10;
    //     })
    //     .attr('font-family', 'Microsoft YaHei')
    //     .attr('font-size', '12px')
    //     .attr('fill', '#9400D3');

    // 设置精度和样式
    var formatPrecision = d3.format('$');

    // 定义X轴
    var xAxis = d3.svg.axis()
        .scale(xScale)

        // 粗略的设置刻度线的数量，包括原点
        .ticks(7)
        .orient('bottom');

    // 定义Y轴
    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient('left')
        ;

    // 创建X轴, svg中： g元素是一个分组元素
    svg.append('g')
        .attr('class', 'axis')

        // 设置据下边界的距离
        .attr('transform', 'translate(0, ' + (svgHight - padding) + ')')
        .call(xAxis)
        .append("text")
        .attr("transform", "translate(" + (svgWidth) + "," + 20 + ")")
        .attr("x", -60)
        .style("text-anchor", "middle")
        .text("degree");

    // 创建Y轴
    svg.append('g')
        .attr('class', 'axis')

        // Y轴离左边界的距离
        .attr('transform', 'translate(' + padding + ', 0)')
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(0)")
        .attr("x", 15)
        .attr("y", 30)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("entity count");

    var line = d3.svg.line()
        .x(function(d) {
            return xScale(d.degreeCount)
        })
        .y(function(d) {
            return yScale(d.resCount);
        })
        .interpolate("cardinal"); //monotone

    svg.append('svg:path')
        .attr('d', line(dataset))
        .attr('stroke', 'green')
        .attr('stroke-width', 2)
        .attr('fill', 'none');





}