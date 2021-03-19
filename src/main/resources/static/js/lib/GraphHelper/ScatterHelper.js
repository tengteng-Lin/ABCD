
function showScatter(dataset,title,divID,tooltip) {
    console.log(dataset)
    var svgWidth = 500;
    var svgHight = 380;
    var padding =  50;



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

    svg.append("text")
        .attr("transform", "translate(" + (svgWidth-10) + "," + 340 + ")")
        .attr("x", -60)
        .attr("font-size",12)
        .style("text-anchor", "middle")
        .text("log(degree)");

    svg.append("text")
        .attr("transform", "rotate(0)")
        .attr("x", 95)
        .attr("y", 30)
        .attr("dy", ".71em")
        .attr("font-size",12)
        .style("text-anchor", "end")
        .text("log(entity count)");


    // 创建比例尺
    var xScale = d3.scaleLinear()
        .domain([d3.min(dataset,function (d) {
            return d.degreeCount-1;

        }), d3.max(dataset, function(d) {

            return d.degreeCount;
        })]).range([padding, svgWidth - padding * 2]);

    var yScale = d3.scaleLinear()
        .domain([0, d3.max(dataset, function(d) {

            return d.resCount;
        })]).range([svgHight - padding, padding]);

    var rScale = d3.scaleLinear()
        .domain([0, d3.max(dataset, function(d) {
            return d.resCount;
        })]).range([2, 4]);




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
    // var xAxis = d3.svg.axis()
    //     .scale(xScale)
    //
    //     // 粗略的设置刻度线的数量，包括原点
    //     .ticks(7)
    //     .orient('bottom');
    var xAxis =d3.axisBottom(xScale)

    // 定义Y轴
    // var yAxis = d3.svg.axis()
    //     .scale(yScale)
    //     .orient('left')
    //     ;
    var yAxis = d3.axisLeft(yScale)

    // 创建X轴, svg中： g元素是一个分组元素
    svg.append('g')
        // .attr('class', 'axis1')


        // 设置据下边界的距离
        .attr('transform', 'translate(0, ' + (svgHight - padding) + ')')
        .call(xAxis)


    // 创建Y轴
    svg.append('g')
        // .attr('class', 'axis1')
        // .attr('fill',"transparent")
        // Y轴离左边界的距离
        .attr('transform', 'translate(' + padding + ', 0)')
        .call(yAxis)


    var line = d3.line()
        .x(function(d) {
            return xScale(d.degreeCount)
        })
        .y(function(d) {
            return yScale(d.resCount);
        })
        .curve(d3.curveMonotoneX)
    // .curve(d3.curveMonotoneX) // apply smoothing to the line

    svg.append('svg:path')
        .attr('d', line(dataset))
        .attr('stroke', "#508DA3")
        .attr('stroke-width', 2)
        .attr('fill', 'none');

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
            return 3
            // return rScale(d.resCount);
        })
        .attr("fill","#808080")
        .on("mouseover",function (d,i) {
            tooltip.html("<span>" + d.example + "</span>")
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20 ) + "px")
                .style("display", "block")
                .style("visibility", "visible");
        })
        .on("mouseout",function (d,i) {
            tooltip.style("visibility", "hidden");

        })





}