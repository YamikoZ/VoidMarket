# VoidMarket

VoidMarket คือปลั๊กอินร้านค้าผู้เล่นแบบ Virtual Shop สำหรับ Paper/Folia ของ VoidSMP

เวอร์ชันนี้ **ไม่มีตลาดกลางของเซิร์ฟเวอร์แล้ว** ไม่มี `/market` ไม่มีสินค้าจากระบบกลาง และไม่มีการซื้อ/ขายกับ server shop อีกต่อไป ทุกการซื้อขายจะเกิดจากร้านค้าผู้เล่นเท่านั้น

ระบบยังมีเศรษฐกิจแบบขึ้น/ลง โดยแสดงเป็นแนวโน้มของร้านค้า:

```text
UP     = ของขายดี / stock ต่ำ / ความต้องการสูง
DOWN   = ของยังไม่ค่อยขาย / stock เยอะ
STABLE = สถานะปกติ
```

แนวโน้มจะแสดงใน GUI ร้านค้า, `/pshop list`, และข้อความหลังซื้อสินค้า

## ฟีเจอร์หลัก

- ร้านค้าผู้เล่นแบบ Virtual Shop
- ไม่ใช้ Chest Shop
- ไม่ผูกกับตำแหน่ง block หรือ chest
- เจ้าของร้าน offline ก็ขายของได้
- Stock เก็บใน database
- เงินเข้าผู้ขายผ่าน Vault Economy
- มีภาษีร้านค้าจาก `tax-percent`
- มี UI แสดงราคา, stock, owner, sold count และ trend
- มีปุ่ม copy shopId จาก `/pshop list`
- รองรับ Paper และ Folia
- รองรับ SQLite และ MySQL/MariaDB
- รองรับภาษา `en` และ `th`
- รองรับ PlaceholderAPI แบบ optional

## สิ่งที่ต้องติดตั้ง

- Paper หรือ Folia รุ่นที่ plugin build รองรับ
- Java ตาม release/pom ของ plugin
- Vault
- Economy plugin เช่น EssentialsX Economy
- LuckPerms แนะนำสำหรับ permission
- PlaceholderAPI ไม่บังคับ

## Folia

VoidMarket รองรับ Folia แล้ว:

```yaml
folia-supported: true
```

โค้ดใช้ Paper/Folia scheduler API แทน `Bukkit.getScheduler()`

## วิธี Build

ในโฟลเดอร์ `VoidMarket`:

```bash
mvn clean package
```

jar จะอยู่ที่:

```text
target/voidmarket-1.0.0.jar
```

## วิธีติดตั้ง

1. ใส่ `voidmarket-1.0.0.jar` ในโฟลเดอร์ `plugins/`
2. ติดตั้ง Vault และ economy plugin เช่น EssentialsX
3. Start server หนึ่งครั้งให้ plugin สร้าง config
4. แก้ `plugins/VoidMarket/config.yml`
5. Restart server หรือใช้ `/pshop reload`

ถ้าอัปเดตจากเวอร์ชันเก่าแล้วข้อความยังเป็น key แปลก ๆ ให้ลบหรือ rename:

```text
plugins/VoidMarket/messages_en.yml
plugins/VoidMarket/messages_th.yml
```

แล้ว restart server เพื่อให้สร้างไฟล์ใหม่

## คำสั่งผู้เล่นที่ใช้บ่อย

```text
/pshop
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop list
/pshop manage
/pshop stock <shopId>
/pshop setprice <shopId> <ราคา>
/pshop remove <shopId>
```

## คำสั่ง Player Shop ทั้งหมด

### `/pshop`

เปิดเมนูหลักของร้านค้าผู้เล่น

Permission:

```text
voidmarket.use
```

### `/pshop help`

ดูวิธีใช้งานร้านค้าผู้เล่น

Permission:

```text
voidmarket.use
```

### `/pshop create <price>`

สร้างร้านจาก item ที่ถืออยู่ในมือ

ตัวอย่าง:

```text
/pshop create 250
```

ขั้นตอน:

1. ถือ item ที่ต้องการขาย
2. พิมพ์ `/pshop create <ราคา>`
3. กดยืนยันใน GUI
4. item ในมือจะกลายเป็น stock เริ่มต้นของร้าน
5. ผู้เล่นอื่นซื้อได้จาก `/pshop browse`

Permission:

```text
voidmarket.shop.create
```

### `/pshop browse`

เปิด GUI ตลาดร้านค้าผู้เล่นทั้งหมด

ใน GUI จะแสดง:

- item icon
- owner
- price
- stock
- sold count
- economy trend: `UP`, `DOWN`, `STABLE`

Permission:

```text
voidmarket.use
```

### `/pshop list`

แสดงร้านของตัวเองใน chat พร้อมปุ่มจัดการ

ตัวอย่าง:

```text
1. Diamond | Price 250 | Stock 64 | Trend UP | ID 4b3f1c9a [COPY ID] [STOCK] [PRICE] [REMOVE]
```

ปุ่ม:

- `[COPY ID]` copy shopId แบบเต็ม
- `[STOCK]` เติมคำสั่ง `/pshop stock <shopId>`
- `[PRICE]` เติมคำสั่ง `/pshop setprice <shopId> `
- `[REMOVE]` เติมคำสั่ง `/pshop remove <shopId>`

Permission:

```text
voidmarket.use
```

### `/pshop manage`

เปิด GUI จัดการร้านของตัวเอง

คลิกขวาที่ร้านเพื่อเปิดหน้า stock

Permission:

```text
voidmarket.use
```

### `/pshop stock <shopId>`

เปิด GUI เติม stock ของร้าน

ตัวอย่าง:

```text
/pshop stock 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

แนะนำให้กดปุ่ม `[STOCK]` จาก `/pshop list`

Permission:

```text
voidmarket.use
```

### `/pshop setprice <shopId> <price>`

เปลี่ยนราคาของร้าน

ตัวอย่าง:

```text
/pshop setprice 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx 500
```

แนะนำให้กดปุ่ม `[PRICE]` จาก `/pshop list`

Permission:

```text
voidmarket.use
```

### `/pshop remove <shopId>`

ลบร้านและคืน stock ที่เหลือเข้า inventory ของเจ้าของร้าน

ตัวอย่าง:

```text
/pshop remove 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

Permission:

```text
voidmarket.shop.remove
```

### `/pshop reload`

Reload config และ messages

Permission:

```text
voidmarket.reload
```

### `/pshop storage`

ดูชนิด storage ที่กำลังใช้ เช่น SQLite หรือ MySQL

Permission:

```text
voidmarket.use
```

## ระบบเศรษฐกิจขึ้น/ลง

VoidMarket ไม่มีตลาดกลางแล้ว แต่ยังมี “แนวโน้มเศรษฐกิจ” ของร้านผู้เล่น

แนวโน้มคำนวณจาก stock และยอดขาย:

- `UP` ถ้าของขายดี หรือ stock ต่ำ
- `DOWN` ถ้ายังไม่ค่อยขาย และ stock เยอะ
- `STABLE` ถ้าสถานะปกติ

แนวโน้มนี้ช่วยให้ผู้เล่นเห็นว่า item ในร้านกำลังได้รับความนิยมไหม

## ทำไมซื้อร้านตัวเองไม่ได้

ถ้าขึ้น:

```text
Transaction cancelled: You cannot buy from your own shop.
```

แปลว่า:

```text
ยกเลิกธุรกรรม: คุณไม่สามารถซื้อของจากร้านตัวเองได้
```

ค่า config ปัจจุบัน:

```yaml
allow-buy-own-shop: false
```

ถ้าต้องการให้ซื้อร้านตัวเองได้:

```yaml
allow-buy-own-shop: true
```

แล้วใช้:

```text
/pshop reload
```

หรือ restart server

## Storage / ฐานข้อมูล

ค่าเริ่มต้นคือ SQLite:

```yaml
storage:
  type: sqlite
  sqlite:
    file: market.db
```

MySQL/MariaDB:

```yaml
storage:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: voidmarket
    username: root
    password: ''
    useSSL: false
```

หมายเหตุ: ชื่อไฟล์ SQLite ยังเป็น `market.db` เพื่อความเข้ากันได้กับข้อมูลเดิม แต่ระบบตลาดกลางถูกลบออกแล้ว

## ภาษา

ค่าเริ่มต้น:

```yaml
language: en
```

ภาษาไทย:

```yaml
language: th
```

ไฟล์ข้อความ:

```text
messages_en.yml
messages_th.yml
```

ถ้าไฟล์ภาษาเก่าขาด key ใหม่ plugin จะ fallback ไปใช้ข้อความ default จาก jar

## Config สำคัญ

```yaml
language: en
tax-percent: 5.0
max-shops-default: 2
max-shops-vip: 5
max-shops-svip: 10
max-shops-staff: 50
max-shop-stock: 3456
allow-buy-own-shop: false
```

ความหมาย:

- `tax-percent` ภาษีร้านค้าผู้เล่น
- `max-shops-default` จำนวนร้านของผู้เล่นทั่วไป
- `max-shops-vip` จำนวนร้านของ VIP
- `max-shops-svip` จำนวนร้านของ SVIP
- `max-shops-staff` จำนวนร้านของ staff
- `max-shop-stock` stock สูงสุดต่อร้าน
- `allow-buy-own-shop` อนุญาตให้ซื้อร้านตัวเองหรือไม่

## Blocked Items

ตั้ง item ที่ห้ามขายในร้าน:

```yaml
blocked-items:
  - BEDROCK
  - COMMAND_BLOCK
```

## Permissions ทั้งหมด

```text
voidmarket.use
voidmarket.shop.create
voidmarket.shop.remove
voidmarket.shop.limit.default
voidmarket.shop.limit.vip
voidmarket.shop.limit.svip
voidmarket.shop.limit.staff
voidmarket.admin
voidmarket.reload
voidmarket.bypass
```

## ตัวอย่าง LuckPerms

```text
lp group default permission set voidmarket.use true
lp group default permission set voidmarket.shop.create true
lp group default permission set voidmarket.shop.limit.default true

lp group vip permission set voidmarket.shop.limit.vip true
lp group svip permission set voidmarket.shop.limit.svip true

lp group admin permission set voidmarket.reload true
lp group admin permission set voidmarket.bypass true
```

## PlaceholderAPI

```text
%voidmarket_shops%
%voidmarket_items_sold%
%voidmarket_shop_trend_<shopId>%
%voidmarket_shop_stock_<shopId>%
```

## สรุปสำหรับผู้เล่น

```text
/pshop
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop list
/pshop manage
```

## สรุปสำหรับแอดมิน

```text
/pshop reload
/pshop storage
```
