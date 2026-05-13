# VoidMarket

VoidMarket คือปลั๊กอินระบบเศรษฐกิจสำหรับเซิร์ฟเวอร์ Minecraft Paper/Folia ของ VoidSMP มีตลาดกลาง, ราคาแบบ Dynamic, ร้านค้าผู้เล่นแบบ Virtual Shop, GUI, ฐานข้อมูล SQLite/MySQL, Vault Economy และ PlaceholderAPI แบบ optional

ร้านค้าผู้เล่นของ VoidMarket ไม่ใช่ Chest Shop ไม่ต้องวางกล่อง และไม่ผูกกับตำแหน่ง block ใด ๆ ทุกอย่างเป็นร้าน virtual ที่เก็บ stock ในฐานข้อมูล

## ฟีเจอร์หลัก

- ตลาดกลางของเซิร์ฟเวอร์ผ่าน `/market`
- ซื้อ/ขายผ่าน GUI และ command
- ราคาเปลี่ยนตาม demand, supply และ stock
- หมวดสินค้า เช่น Blocks, Food, Tools, Ores, Mob Drops, Special
- ร้านค้าผู้เล่นแบบ Virtual Shop ผ่าน `/pshop`
- เจ้าของร้าน offline ก็ขายของได้
- เงินเข้าผู้ขายผ่าน Vault Economy
- มีภาษีร้านค้า `tax-percent`
- รองรับภาษา `en` และ `th`
- รองรับ legacy color `&a`, hex และ MiniMessage gradient
- รองรับ Paper และ Folia
- SQLite เป็นค่าเริ่มต้น
- รองรับ MySQL/MariaDB
- มี transaction log
- รองรับ PlaceholderAPI ถ้าติดตั้งไว้
- `/pshop list` มีปุ่ม `[COPY ID]` เพื่อ copy shopId ได้ทันที

## สิ่งที่ต้องติดตั้ง

- Paper หรือ Folia รุ่นที่ plugin build รองรับ
- Java ตามเวอร์ชันที่ระบุใน release/pom ปัจจุบัน
- Vault
- Economy plugin เช่น EssentialsX Economy
- LuckPerms แนะนำสำหรับจัด permission
- PlaceholderAPI ไม่บังคับ

## Folia

VoidMarket รองรับ Folia แล้ว โดยใน `plugin.yml` มี:

```yaml
folia-supported: true
```

และโค้ดใช้ Paper/Folia scheduler API แทน `Bukkit.getScheduler()`

## วิธี Build

ในโฟลเดอร์ `VoidMarket` ใช้:

```bash
mvn clean package
```

ไฟล์ jar จะอยู่ที่:

```text
target/voidmarket-1.0.0.jar
```

## วิธีติดตั้ง

1. นำ `voidmarket-1.0.0.jar` ไปใส่ในโฟลเดอร์ `plugins/`
2. ติดตั้ง Vault และ economy plugin เช่น EssentialsX
3. Start server หนึ่งครั้งเพื่อสร้างไฟล์ config
4. แก้ `plugins/VoidMarket/config.yml`
5. Restart server หรือใช้ `/market reload`

ถ้าอัปเดต plugin แล้วข้อความบางอย่างยังเป็น key แปลก ๆ เช่น `shop-list-header` ให้ลบหรือ rename ไฟล์ message เก่า:

```text
plugins/VoidMarket/messages_en.yml
plugins/VoidMarket/messages_th.yml
```

แล้ว restart server เพื่อให้ plugin สร้างไฟล์ใหม่จาก jar

## คำสั่งที่ผู้เล่นใช้บ่อย

```text
/market
/market prices
/market buy <item> <amount>
/market sell <item> <amount>
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop list
/pshop manage
```

## คำสั่ง Market ทั้งหมด

### `/market`

เปิด GUI ตลาดกลาง

Permission:

```text
voidmarket.use
```

### `/market help`

ดูคำสั่งของระบบตลาด

Permission:

```text
voidmarket.use
```

### `/market buy <item> <amount>`

ซื้อของจากตลาดกลาง

ตัวอย่าง:

```text
/market buy diamond 1
/market buy stone 64
```

Permission:

```text
voidmarket.buy
```

### `/market sell <item> <amount>`

ขายของเข้าตลาดกลาง

ตัวอย่าง:

```text
/market sell diamond 1
/market sell stone 64
```

Permission:

```text
voidmarket.sell
```

### `/market price <item>`

ดูราคาซื้อ ราคาขาย และ stock ของ item

ตัวอย่าง:

```text
/market price diamond
/market price iron_ingot
```

Permission:

```text
voidmarket.use
```

### `/market prices`

เปิด GUI รายการราคาสินค้า

Permission:

```text
voidmarket.use
```

### `/market storage`

ดูชนิดฐานข้อมูลที่ใช้งานอยู่ เช่น SQLite หรือ MySQL

Permission:

```text
voidmarket.use
```

### `/market admin`

เปิด GUI แอดมินของตลาด

Permission:

```text
voidmarket.admin
```

### `/market reload`

Reload config และ messages

Permission:

```text
voidmarket.reload
```

### `/market migrate`

ตรวจ/รัน schema migration

Permission:

```text
voidmarket.admin
```

### `/market backup`

คำสั่งสำหรับตรวจ/รัน backup ฐานข้อมูล

Permission:

```text
voidmarket.admin
```

## คำสั่ง Player Shop ทั้งหมด

Player Shop เป็นร้าน virtual ทั้งหมด ไม่มี chest และไม่ต้องวาง block

### `/pshop`

เปิดเมนูร้านค้าของตัวเอง

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
2. ใช้ `/pshop create <ราคา>`
3. กดยืนยันใน GUI
4. item ในมือจะกลายเป็น stock เริ่มต้นของร้าน
5. ผู้เล่นอื่นซื้อได้จาก `/pshop browse`

Permission:

```text
voidmarket.shop.create
```

### `/pshop browse`

เปิด GUI ดูร้านค้าผู้เล่นทั้งหมด

Permission:

```text
voidmarket.use
```

### `/pshop list`

แสดงร้านของตัวเองใน chat พร้อมปุ่มคลิก

ตัวอย่างที่จะแสดง:

```text
1. Diamond | Price 250 | Stock 64 | ID 4b3f1c9a [COPY ID] [STOCK] [PRICE] [REMOVE]
```

ปุ่ม:

- `[COPY ID]` copy shopId แบบเต็มทันที
- `[STOCK]` เติมคำสั่ง `/pshop stock <shopId>` ในช่อง chat
- `[PRICE]` เติมคำสั่ง `/pshop setprice <shopId> ` ในช่อง chat เพื่อให้ใส่ราคา
- `[REMOVE]` เติมคำสั่ง `/pshop remove <shopId>` ในช่อง chat

`ID 4b3f1c9a` เป็นรหัสย่อให้อ่านง่าย ส่วนระบบจริงยังใช้ UUID เต็มเพื่อไม่ให้ร้านชนกัน

Permission:

```text
voidmarket.use
```

### `/pshop manage`

เปิด GUI จัดการร้านของตัวเอง

ใน GUI:

- ดูร้านของตัวเอง
- คลิกขวาที่ร้านเพื่อเปิดเมนู stock
- ใช้ร่วมกับ `/pshop list` เพื่อ copy shopId ได้ง่าย

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

แนะนำให้ใช้ปุ่ม `[STOCK]` จาก `/pshop list` แทนการพิมพ์ UUID เอง

Permission:

```text
voidmarket.use
```

### `/pshop setprice <shopId> <price>`

เปลี่ยนราคาขายของร้าน

ตัวอย่าง:

```text
/pshop setprice 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx 500
```

แนะนำให้ใช้ปุ่ม `[PRICE]` จาก `/pshop list`

Permission:

```text
voidmarket.use
```

### `/pshop remove <shopId>`

ลบร้านและคืน stock ที่เหลือกลับเข้า inventory ของเจ้าของร้าน

ตัวอย่าง:

```text
/pshop remove 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

แนะนำให้ใช้ปุ่ม `[REMOVE]` จาก `/pshop list`

Permission:

```text
voidmarket.shop.remove
```

## วิธีตั้งร้านแบบสั้น

ส่งให้ผู้เล่นอ่านได้เลย:

```text
1. ถือ item ที่ต้องการขาย
2. พิมพ์ /pshop create <ราคา>
3. กดยืนยันใน GUI
4. ใช้ /pshop list เพื่อ copy shopId หรือกดปุ่มจัดการ
5. ใช้ /pshop browse เพื่อดูร้านคนอื่น
```

## ทำไมซื้อร้านตัวเองไม่ได้

ถ้าขึ้นข้อความ:

```text
Transaction cancelled: You cannot buy from your own shop.
```

แปลว่า:

```text
ยกเลิกธุรกรรม: คุณไม่สามารถซื้อของจากร้านตัวเองได้
```

ค่า config ปัจจุบันคือ:

```yaml
allow-buy-own-shop: false
```

ถ้าต้องการให้เจ้าของร้านซื้อของตัวเองได้ ให้เปลี่ยนเป็น:

```yaml
allow-buy-own-shop: true
```

แล้วใช้:

```text
/market reload
```

หรือ restart server

## ระบบราคา Dynamic

ราคาตลาดกลางคำนวณจาก:

- คนซื้อเยอะ ราคาเพิ่ม
- คนขายเยอะ ราคาลด
- stock ต่ำ ราคาเพิ่ม
- stock สูง ราคาถูกลง
- ราคาถูกจำกัดด้วย min/max multiplier

สูตรแนวคิด:

```text
priceMultiplier =
  1.0
  + demandFactor
  - supplyFactor
  + scarcityFactor
```

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

ตารางจะถูกสร้างอัตโนมัติเมื่อ plugin เปิด

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

ถ้าไฟล์ภาษาของ server เก่าและขาด key ใหม่ plugin จะ fallback ไปใช้ข้อความ default จาก jar

## Config สำคัญ

```yaml
language: en
tax-percent: 5.0
daily-stock-limit: 5000
max-shops-default: 2
max-shops-vip: 5
max-shops-svip: 10
max-shops-staff: 50
max-shop-stock: 3456
allow-buy-own-shop: false
```

ความหมาย:

- `tax-percent` ภาษีร้านค้าผู้เล่น
- `daily-stock-limit` จำกัด stock รายวันของ market
- `max-shops-default` จำนวนร้านของผู้เล่นทั่วไป
- `max-shops-vip` จำนวนร้านของ VIP
- `max-shops-svip` จำนวนร้านของ SVIP
- `max-shops-staff` จำนวนร้านของ staff
- `max-shop-stock` stock สูงสุดต่อร้าน
- `allow-buy-own-shop` อนุญาตให้ซื้อร้านตัวเองหรือไม่

## Blocked Items

ตั้ง item ที่ห้ามซื้อขาย:

```yaml
blocked-items:
  - BEDROCK
  - COMMAND_BLOCK
```

## หมวดหมู่สินค้า

```yaml
categories:
  Blocks:
    - STONE
    - OAK_LOG
  Food:
    - BREAD
    - COOKED_BEEF
  Ores:
    - COAL
    - IRON_INGOT
    - DIAMOND
```

## Permissions ทั้งหมด

```text
voidmarket.use
voidmarket.buy
voidmarket.sell
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
lp group default permission set voidmarket.buy true
lp group default permission set voidmarket.sell true
lp group default permission set voidmarket.shop.create true
lp group default permission set voidmarket.shop.limit.default true

lp group vip permission set voidmarket.shop.limit.vip true
lp group svip permission set voidmarket.shop.limit.svip true

lp group admin permission set voidmarket.admin true
lp group admin permission set voidmarket.reload true
lp group admin permission set voidmarket.bypass true
```

## PlaceholderAPI

```text
%voidmarket_price_<item>%
%voidmarket_stock_<item>%
%voidmarket_trend_<item>%
%voidmarket_shops%
%voidmarket_daily_volume_<item>%
```

ตัวอย่าง:

```text
%voidmarket_price_diamond%
%voidmarket_stock_diamond%
%voidmarket_trend_diamond%
```

## สรุปสำหรับผู้เล่น

ผู้เล่นทั่วไปใช้ประมาณนี้:

```text
/market
/market prices
/market buy <item> <amount>
/market sell <item> <amount>
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop list
/pshop manage
```

## สรุปสำหรับแอดมิน

แอดมินควรรู้:

```text
/market admin
/market reload
/market storage
/market migrate
/market backup
```

และควรให้ permission พื้นฐาน:

```text
voidmarket.use
voidmarket.buy
voidmarket.sell
voidmarket.shop.create
voidmarket.shop.limit.default
```
