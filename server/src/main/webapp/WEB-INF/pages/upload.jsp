<%@ page import="eu.peppol.persistence.api.account.Account" %>
<%--
  Created by IntelliJ IDEA.
  User: steinar
  Date: 24.11.11
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account account = (Account) request.getAttribute(Account.class.getSimpleName());
%>
<html>
<head>
    <title>PEPPOL upload</title>
    <style type="text/css">
        div.main {
            text-align: center;
        }

        div.content {
            text-align: left;
            width: 500px;
            margin: auto;
        }

        label {
            text-align: right;
            display: inline-block;
            width: 200px;
        }

        input[type='submit'] {
            float: right;
        }
    </style>
</head>
<body>
<div class="main">
    <div class="content">
        <h1>Upload and send electronic messages</h1>

        <p>Specify the routing parameters and the invoice and press OK.</p>

        <form action="../outbox" enctype="multipart/form-data" method="post"  >

            <fieldset>
                <legend>Routing information</legend>

                <label for="RecipientID">RecipientId:</label>
                <input type="text" id="RecipientID" name="RecipientID" value="9908:">
                <br/>
                <label for="SenderID">SenderId:</label>
                <input type="text" id="SenderID" name="SenderID" value="9908:">
                <br/>
                <label for="ChannelID">ChannelID:</label>
                <input type="text" id="ChannelID" name="ChannelID" value="UPLOAD_TEST"/>
                <br/>

                <label for="ProcessID">ProcessID:</label>
                <select id="ProcessID" name="ProcessID">
                    <option value="INVOICE_ONLY">Invoice</option>
                    <option value="ORDER_ONLY">Order</option>
                </select>
                <br/>

                <label for="DocumentID">DocumentID:</label>
                <select id="DocumentID" name="DocumentID">
                    <option value="INVOICE">Invoice</option>
                    <option value="CREDIT_NOTE">Credit invoice</option>
                    <option value="ORDER">Order</option>
                </select>
                <br/>
                <label for="filename">File name (really):</label>
                <input type="file" id="filename" name="file"/>
                <br/>
                <input type="submit" value="Submit"/>
            </fieldset>
        </form>
    </div>
</div>
</form>
</body>
</html>