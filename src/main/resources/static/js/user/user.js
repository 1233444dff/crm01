layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render({
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });

    /*条件查询*/
    $(".search_btn").click(function(){
        //这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                userName: $("input[name='userName']").val(),
                phone: $("input[name='phone']").val(),
                email:$("#email").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    /*绑定头部工具栏*/
    //触发事件
    table.on('toolbar(users)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
                openAddOrUpdateUserDialog();
                break;
            case 'del':
               // layer.msg('删除');
                deleteUser(checkStatus.data);
                break;
        }

    });


    /**
     * 删除
     */
    function  deleteUser(datas){
        if(datas.length==0){
            layer.msg("请选择数据");
            return ;
        }
        layer.confirm("你确定要删除这些数据?",{
            btn:["确认","取消"],
        },function(index){
            //关闭确认框
            layer.close(index);
            //收集数据
            var ids=[];
            //遍历
            for(var x in datas){
                ids.push(datas[x].id);
            }
            /*发送ajax删除*/
            $.ajax({
                type:"post",
                url:ctx+"/user/delete",
                data:{"ids":ids.toString()},
                success:function (result){
                    if(result.code==200){
                        layer.msg("删除成功了",{icon:6});
                        //刷新一下
                        parent.location.reload();
                    }else{
                        //提示一下
                        layer.msg(result.msg,{icon: 5 });
                    }
                }
            });
        });
    }

    function  openAddOrUpdateUserDialog(userId){
        var title="<h2>用户模块--添加</h2>";
        var url=ctx+"/user/addOrUpdateDialog";
        //判断添加还是修改
        if(userId){
            title="<h2>用户模块--更新</h2>";
            url=url+"?userId="+userId;
        }
        layer.open({
            title:title,
            type:2,//iframe
            content:url,
            maxmin:true,
            area:["550px","620px"]
        });
    }

    /*绑定行内工具栏*/

    table.on('tool(users)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if(layEvent === 'edit'){ //查看
            openAddOrUpdateUserDialog(data.id);
        } else if(layEvent === 'del'){ //删除
            layer.confirm('真的删除行么', function(index){

                layer.close(index);
                //向服务端发送删除指令
                $.ajax({
                    type:"post",
                    url:ctx+"/user/delete",
                    data:{"ids":data.id},
                    success:function (result){
                        if(result.code==200){
                            layer.msg("删除成功了",{icon:6});
                            //刷新一下
                            parent.location.reload();
                        }else{
                            //提示一下
                            layer.msg(result.msg,{icon: 5 });
                        }
                    }
                });
            });
        }
    });

});