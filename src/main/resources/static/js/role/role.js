layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click",function(){
        table.reload("roleListTable",{
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                roleName: $("input[name='roleName']").val()
            }
        })
    });

    /*头部工具栏*/
    //触发事件
    table.on('toolbar(roles)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'add':
                openAddOrUpdateRoleDialog();
                break;
            case 'grant':
                //layer.msg('授权');
                openAddGrantDialog(checkStatus.data);
                break;
        }
    });


    function  openAddGrantDialog(datas){
        //为选择
        if(datas.length==0){
            layer.msg("请选择授权的角色");
            return ;
        }
        //多个角色一起授权的判断
        if(datas.length>1){
            layer.msg("暂不支持批量授权");
            return ;
        }
        console.log(datas);

        layer.open({
            title:"<h2>角色模块--授权</h2>",
            type:2,
            area:["600px","280px"],
            maxmin:true,
            content: ctx+"/role/toRoleGrantPage?roleId="+datas[0].id
        });

    }


    function openAddOrUpdateRoleDialog(roleId){
        var title="<h2>角色模块--添加</h2>";
        var url=ctx+"/role/addOrUpdateRole";
        //判断添加或者修改
        if(roleId){
            title="<h2>角色模块--更新</h2>";
            url=url+"?roleId="+roleId;
        }
        //弹出层
        layui.layer.open({
            title:title,
            type:2,
            maxmin:true,
            area:["600px","280px"],
            content:url
        });
    }


    /*行内工具栏*/

    table.on('tool(roles)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if(layEvent === 'del'){ //删除
            layer.confirm('真的删除行么', function(index){
                //发送ajax删除数据
                $.post(ctx+"/role/delete",{"roleId":data.id},function(res){
                    //判断
                    if(res.code == 200){
                        //layer.msg("删除成功了");
                        layer.close(index);
                        //刷新
                        tableIns.reload();
                    }else{
                        //提示一下
                        layer.msg(res.msg,{icon:5 });
                    }
                },"json");
            });
        } else if(layEvent === 'edit'){ //编辑
            //do something
            //layer.msg("修改");
            openAddOrUpdateRoleDialog(data.id);
            return ;
        }
    });

});