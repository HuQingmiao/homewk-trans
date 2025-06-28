$(function () {

    layui.use(['form', 'table', 'laydate', 'layer'], function (form, table, laydate, layer) {


        var form = layui.form;
        var laydate = layui.laydate;

        // 获取当前日期并减去20天
        var today = new Date();
        today.setDate(today.getDate() - 20);
        var year = today.getFullYear();
        var month = String(today.getMonth() + 1).padStart(2, '0');
        var day = String(today.getDate()).padStart(2, '0');
        var hours = String(today.getHours()).padStart(2, '0');
        var minutes = String(today.getMinutes()).padStart(2, '0');
        var formattedTime1 = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes ;

        // 设置transBeginTime的默认值
        $('#transBeginTime').val(formattedTime1);


        // 交易时间止 增加1天
        today = new Date();
        today.setDate(today.getDate() + 1);
        year = today.getFullYear();
        month = String(today.getMonth() + 1).padStart(2, '0');
        day = String(today.getDate()).padStart(2, '0');
        hours = String(today.getHours()).padStart(2, '0');
        minutes = String(today.getMinutes()).padStart(2, '0');
        var formattedTime2 = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;

        // 将时间设置到输入框中
        $('#transEndTime').val(formattedTime2);


        // 渲染日期时间选择器
        laydate.render({
            elem: '#transBeginTime',
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm'
        });

        laydate.render({
            elem: '#transEndTime',
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm'
        });


        ///查询字段变化时，动态生成查询框内容
        form.on('select(qryField)', function(data){
            var value = data.value;
            var html = '<input type="text" name="qryText"  placeholder="请输入查询内容" autocomplete="off" class="layui-input">';
            switch (value) {
                case 'transType':
                    html = `
                        <select name="qryText" lay-filter="qryText">
                        <option value="DEPOSIT">存款</option>
                        <option value="WITHDRAWAL">取款</option>
                        <option value="TRANSFER">转账</option>
                    </select>
                    `;
                    break;
                case 'transChannel':
                    html = `
                        <select name="qryText" lay-filter="qryText">
                            <option value="ATM">ATM机</option>
                            <option value="POS">POS机</option>
                            <option value="MOBILE_APP">网银APP</option>
                        </select>
                    `;
                    break;
            }

            $('#qry_text_box').html(html);
            form.render('select');
        });

        // 查询按钮
        form.on('submit(query_list_btn)', function (data) {
            var field = data.field; // 获取表单字段值

            table.reload('transactionTable', {
                page: {
                    pageNo: 1,
                    curr: 1
                },
                where: { // 传递数据异步请求时携带的字段
                    qryField: field.qryField,
                    qryText: field.qryText,
                    transBeginTime: new Date(field.transBeginTime).toISOString(),
                    transEndTime: new Date(field.transEndTime).toISOString()
                },
            });

            return false; // 阻止默认 form 跳转
        });

        /*
        // 这里是测试数据，如果开启测试数据需要注释掉table.render的url同时开启data参数
        var tableData = [];
        for (var i = 0; i < 10; i++) {
            tableData.push({
                id: i,
                transId: '1234567890' + i,
                accountNo: '1234567890',
                transType: 'DEPOSIT',
                transChannel: 'online',
                transTime: '2020-01-01 12:00:00',
                amount: '100.00',
                currency: 'CNY',
                balanceAfter: '1000.00'
            });
        }
        */
        var tableId = 'transactionTable';
        // 表格渲染
        table.render({
            elem: `#${tableId}`,
            id: tableId,
            url: '/api/transaction', // 接口请求
            // data: tableData,      // 测试数据，注释掉上面的url参数开启
            height: 'full',
            page: true,
            cols: [[
                {type: 'checkbox', fixed: 'left'},
                {field: 'id', title: '交易ID',  align: 'center',  width: 180},
                {field: 'accountNo', title: '交易账号',  align: 'center', width: 160},
                {
                    field: 'transType', title: '交易类型',  align: 'center', width: 90, templet: function (d) {
                        switch (d.transType) {
                            case 'DEPOSIT':
                                return '存款';
                            case 'WITHDRAWAL':
                                return '取款';
                            case 'TRANSFER':
                                return '转账';
                            default:
                                return d.transType;
                        }
                    }
                },
                {
                    field: 'transChannel', title: '交易渠道', width: 90,  align: 'center',  templet: function (d) {
                        switch (d.transChannel) {
                            case 'ATM':
                                return 'ATM机';
                            case 'POS':
                                return 'POS机';
                            case 'MOBILE_APP':
                                return '网银APP';
                            default:
                                return d.transChannel;
                        }
                    }
                },
                // {field: 'transTime', title: '交易时间', width: 180},
                {
                    field: 'transTime',
                    title: '交易时间',
                    width: 160,
                    align: 'center',
                    templet: function(d) {
                        var date = new Date(d.transTime);
                        var year = date.getFullYear();
                        var month = ("0" + (date.getMonth() + 1)).slice(-2);
                        var day = ("0" + date.getDate()).slice(-2);
                        var hours = ("0" + date.getHours()).slice(-2);
                        var minutes = ("0" + date.getMinutes()).slice(-2);
                        var seconds = ("0" + date.getSeconds()).slice(-2);

                        return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
                    }
                },

                {
                    field: 'amount',
                    title: '金额     ',
                    width: 105,
                    align: 'right', // 设置为右对齐
                    templet: function(d){
                        return d.amount.toFixed(2); // 保持两位小数点
                    }
                },

                {field: 'currency', width: 60, align: 'center', title: '币种'},

                // {field: 'balanceAfter', title: '交易后余额'},
                {
                    field: 'balanceAfter',
                    title: '交易后余额 ',
                    width: 110,
                    align: 'right', // 设置为右对齐
                    templet: function(d){
                        return d.balanceAfter.toFixed(2); // 保持两位小数点
                    }
                },

                {fixed: 'right', title: '操作', width: 130, minWidth: 120, templet: '#table_opt_toolbar'}
            ]],
            // where: {},
            request: {
                pageName: 'pageNo',
                limitName: 'pageSize'
            },
            parseData: function (res) {
                return {
                    "code": res.success ? 0 : -1, // 解析接口状态
                    "msg": res.errorMsg, // 解析提示文本
                    "count": res.data.totalCount, // 解析数据长度
                    "data": res.data.dataList // 解析数据列表
                };
            },
            done: function () {
            }
        });

        // 表格操作栏按钮
        table.on(`tool(${tableId})`, function (obj) {
            switch (obj.event) {
                case 'edit':
                    handleAddOrEdit(2, obj.data);
                    break;
                case 'del':
                    handleDeleteById(obj.data.accountNo, obj.data.id);
                    break;
            }
        });

        // // 监听金额输入框的输入事件
        // $('#amount').on('input', function() {
        //     var maxLength = 14; // 设置最大长度
        //     if ($(this).val().length > maxLength) {
        //         $(this).val($(this).val().slice(0, maxLength));
        //     }
        // });
        //
        // $('#balanceAfter').on('input', function() {
        //     var maxLength = 14; // 设置最大长度
        //     if ($(this).val().length > maxLength) {
        //         $(this).val($(this).val().slice(0, maxLength));
        //     }
        // });

        //// 顶部工具栏
        // 新增按钮
        $('#addData').click(function () {
            handleAddOrEdit(1);
        });

        // 删除按钮
        $('#delData').click(function () {
            var data = getSelectData();
            if (data.length !== 1) {
                layer.msg('请选择一条要删除的交易记录');
                return;
            }
            var d = data[0];
            handleDeleteById(d.accountNo, d.id);
        });


        /**
         * 新增或编辑
         * @param type 1: 新增 2: 编辑
         * @param initData  编辑时反填数据
         */
        function handleAddOrEdit(type, initData) {
            var isAdd = type === 1;
            var tipText = isAdd ? '新增' : '编辑';

            layer.open({
                title: `${tipText}交易`,
                type: 1,
                content: $("#add_transaction").html(),
                shade: 0.1,
                area: ['680px', '540px'],
                btnAlign: 'r',
                btn: ['提交', '取消'],
                zIndex: 999,
                yes: function (layIndex, layero) {
                    var isValid = form.validate('#add_transaction_filter');
                    if (isValid) {
                        var data = form.val('add-transaction-filter');
                        var accountNoLen = data.accountNo.length;
                        if (accountNoLen < 10 || accountNoLen > 20) {
                            layer.msg('交易账号长度应为10-20位', {icon: 2});
                            return;
                        }
                        showLoading();
                        // 处理交易时间以确保使用本地时间
                        var transTime = new Date(data.transTime);
                        var transTimeUTC = new Date(transTime.getTime() - transTime.getTimezoneOffset() * 60000);
                        $.ajax({
                            url: isAdd ? '/api/transaction' : `/api/transaction/${initData.id}`,
                            type: isAdd ? 'post' : 'put',
                            data: JSON.stringify(Object.assign({}, data, {
                                amount: parseFloat(data.amount),
                                balanceAfter: parseFloat(data.balanceAfter),
                                transTime: transTimeUTC.toISOString()
                            })),
                            contentType: 'application/json;charset=UTF-8',
                            success: function (res) {
                                hideLoading();
                                if (res.success) {
                                    layer.msg(`${tipText}成功`, {icon: 1});
                                    reloadTable();
                                } else {
                                    layer.msg(`${tipText}失败:${res.errorMsg}`);
                                }
                            },
                            error: function (err) {
                                layer.msg('新增失败:' + err.status);
                                hideLoading();
                            }
                        });
                    }
                },
                success: function (layero, index) {
                    // 编辑状态
                    if (initData) {
                        form.val('add-transaction-filter', initData);
                    }
                    form.render('select');

                    if (isAdd) {
                        var now = new Date();
                        var year = now.getFullYear();
                        var month = ("0" + (now.getMonth() + 1)).slice(-2);
                        var day = ("0" + now.getDate()).slice(-2);
                        var hours = ("0" + now.getHours()).slice(-2);
                        var minutes = ("0" + now.getMinutes()).slice(-2);
                        var seconds = ("0" + now.getSeconds()).slice(-2);
                        var currentTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
                        $('#add_transTime').val(currentTime);
                    }
                    else{
                        var dt = initData.transTime;
                        var formattedDateStr = dt.replace('T', ' ');
                        $('#add_transTime').val(formattedDateStr);
                    }

                    // 初始化日期
                    laydate.render({
                        elem: '#add_transTime',
                        type: 'datetime',
                        format: 'yyyy-MM-dd HH:mm:ss'
                    });
                }
            });
        }



        /// 删除函数
        function handleDeleteById(accountNo, id) {
            layer.confirm(`您确定要删除交易ID为 [${id}] 的这条记录吗？`, {
                btn: ['确定', '关闭'] //按钮
            }, function () {
                showLoading();
                $.ajax({
                    url: `/api/transaction/${id}`,
                    type: 'delete',
                    data: {
                        id: id
                    },
                    success: function (res) {
                        hideLoading();
                        if (res.success) {
                            layer.msg('删除成功', {icon: 1});
                            reloadTable();
                        } else {
                            layer.msg('删除失败:' + res.errorMsg);
                        }
                    },
                    error: function (err) {
                        layer.msg('删除失败:' + err.status);
                        hideLoading();
                    }
                });
            });
        }

        // 重新加载表格
        function reloadTable() {
            table.reload(tableId);
        }

        // 获取选中数据
        function getSelectData() {
            return table.checkStatus(tableId).data;
        }


        // 加载遮罩层
        var loadIndex;

        function showLoading() {
            loadIndex = layer.load(0);
        }

        function hideLoading() {
            layer.close(loadIndex);
        }
    });

})
;