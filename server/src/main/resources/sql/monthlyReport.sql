select document_id as CustomizationID, sum(if(direction='IN',1,0)) 'Mottatt', sum(if(direction='OUT',1,0)) 'Sendt'
from message
left join (select m1.message_uuid
from message m1
join message m2 on m1.message_uuid = m2.message_uuid
where m1.msg_no != m2.msg_no
and m1.direction = 'IN'
and year(m1.received) = ?
and month(m1.received) = ?) internal on internal.message_uuid = message.message_uuid
where year(received) = ?
and month(received) = ?
and internal.message_uuid is null
and receiver != '9908:976098897'
group by 1;