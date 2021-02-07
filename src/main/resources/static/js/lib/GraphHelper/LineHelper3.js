
function showLine(data,title,divID) {
    console.log(title)
    // SVG画布边缘与图表内容的距离
    var margin = { top: 60, right: 30, bottom: 70, left: 50 },
        width = 400 - margin.left - margin.right,
        height = 300 - margin.top - margin.bottom;

    // 3、创建容器并移动位置到合适(注意：这点因为append了一个g所以返回的svg其实是这个g，后面的元素都在这里，定位也是相对于他)
    var main = d3.select(divID).append("svg")
        .attr("class", "chart")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    // 创建x轴的比例尺(线性比例尺)
    var xScale2 = d3.scale.linear()
        .domain([1,1200])
        .range([0, width - margin.left - margin.right]);

    var xScale = d3.scale.ordinal()
        .domain(data.map(function(d) {
            console.log(d)
            // console.log(d.degreeCount.substring(0,d.degreeCount.indexOf("~")))
            return d.degreeCount; }))
        //.rangeRoundBands([0,width], .1,1);//两种效果不一样
        .rangeRoundBands([0, width - margin.left - margin.right], 1);

// 创建y轴的比例尺(线性比例尺)
    // 5、定义计算Y轴的比例尺的方法
    var yScale = d3.scale.linear()
        .domain([0, d3.max(data, function(d) { return d.resCount+1; })])
        .range([height, 0]);

// 6、创建X坐标轴函数
    var xAxis = d3.svg.axis()
        .scale(xScale2)
        .orient("bottom");

    // 7、创建Y轴坐标轴函数
    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    // 10、加图表标题
    main.append("text")
        .attr("x", (width / 2))
        .attr("y", -40)
        .attr("text-anchor", "middle")
        .style("font-size", "16px")
        .style("text-decoration", "underline")
        .text(title);


    main.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
        .append("text")
        .attr("transform", "translate(" + (width) + "," + 20 + ")")
        .style("text-anchor", "middle")
        .text("degree");

    // 如果x轴名称太长将其倾斜
    main.selectAll(".axis g text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-35)");



    // 100、添加Y坐标轴及坐标轴名称
    main.append("g")
        .attr("class", "axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(0)")
        .attr("x", 50)
        .attr("y", -20)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("entity count");

    //     // 添加折线
    var line = d3.svg.line()
        .x(function(d) {
            return xScale(d.degreeCount)
        })
        .y(function(d) {
            return yScale(d.resCount);
        })
        .interpolate("basis");

    main.append('svg:path')
        .attr('d', line(data))
        .attr('stroke', 'green')
        .attr('stroke-width', 2)
        .attr('fill', 'none');

    var g = main . selectAll ( 'circle' )
        . data ( data )
        . enter ( )
        . append ( 'g' )
        . append ( 'circle' )
        . attr ( 'class' , 'linecircle' )
        . attr ( 'cx' , line . x ( ) )
        . attr ( 'cy' , line . y ( ) )
        . attr ( 'r' , 2.5 )
        // .attr("text-anchor","middle")
        // .attr('text',function (d) {
        //     return d.resCount
        // })
        .attr('fill','#CD5C5C')
        . on ( 'mouseover' , function (d) {
            // console.log(d)
            // tooltip.html("<span>" + d.resCount + "</span>")
            //     .style("left", (d3.event.pageX) + "px")
            //     .style("top", (d3.event.pageY + 20) + "px")
            //     .style("display", "block")
            //     .style("visibility", "visible");

            d3 . select ( this ) . transition ( ) . duration ( 500 ) . attr ( 'r' , 5 ) ;
        } )
        . on ( 'mouseout' , function ( ) {
            // tooltip.style("visibility", "hidden");

            d3 . select ( this ) . transition ( ) . duration ( 500 ) . attr ( 'r' , 3.5 ) ;
        } ) ;




}