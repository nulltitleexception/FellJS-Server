local arg = table.pack(...)
local cols={}
local xLen = (arg[1]-1) or 1
local yLen = (arg[2]-1) or 1

-- Heres where the generation happens (above is boilerplate)
--First we fill it in with -1 (null)
for a=0,xLen,1 do
	cols[a] = {}
	for b=0,yLen,1 do
		cols[a][b] = -1;
	end
end

local roomID = 0;

function getRoomID()
	roomID = roomID+1
	return roomID
end

-- Next we place the rooms in

function placeRoom()
	local x = (math.random(1,xLen/2) * 2) - 1
	local y = (math.random(1,yLen/2) * 2) - 1
	local w = (math.random(2,6) * 2)
	local h = (math.random(2,6) * 2)
	for a=x,x+w,1 do
		for b=y,y+h,1 do
			if ( a > xLen or b > yLen or cols[a][b] ~= -1 ) then
				return
			end
		end
	end
	local rid = getRoomID()
	for a=x,x+w,1 do
		for b=y,y+h,1 do
			cols[a][b] = rid;
		end
	end
end

for i=0,200,1 do
	placeRoom()
end

-- Now it's time for the maze

function canBranch(x, y, dir)
	if (dir == 1) then
		return ((y + 2 <= yLen) and (cols[x][y+1] == -1) and (cols[x][y+2] == -1))
	elseif (dir == 2) then
		return ((x + 2 <= xLen) and (cols[x+1][y] == -1) and (cols[x+2][y] == -1))
	elseif (dir == 3) then
		return ((y - 2 >= 0) and (cols[x][y-1] == -1) and (cols[x][y-2] == -1))
	elseif (dir == 4) then
		return ((x - 2 >= 0) and (cols[x-1][y] == -1) and (cols[x-2][y] == -1))
	else
		print "Lua Error: perfectmaze.lua->canBranch()"
	end
end

function branch(x, y, id)
	while(canBranch(x,y,1) or canBranch(x,y,2) or canBranch(x,y,3) or canBranch(x,y,4)) do
		local dir = math.random(1,4)
		if (canBranch(x,y,dir)) then
			if (dir == 1) then
				cols[x][y+1] = id
				cols[x][y+2] = id
				branch(x, y+2, id)
			elseif (dir == 2) then
				cols[x+1][y] = id
				cols[x+2][y] = id
				branch(x+2, y, id)
			elseif (dir == 3) then
				cols[x][y-1] = id
				cols[x][y-2] = id
				branch(x, y-2, id)
			elseif (dir == 4) then
				cols[x-1][y] = id
				cols[x-2][y] = id
				branch(x-2, y, id)
			else
				print "Lua Error: dungeon.lua->branch()"
			end
		end
	end
end

branch((math.random((xLen-1)/2)*2)+1, (math.random((yLen-1)/2)*2)+1, getRoomID())

for a=1,xLen,2 do
	for b=1,yLen,2 do
		if (cols[a][b] == -1) then
			branch(a, b, getRoomID())
		end
	end
end

-- And now we connect the rooms and maze(s)

function getID()
	for a=0,xLen,1 do
		for b=0,yLen,1 do
			if (cols[a][b] > 0) then
				return cols[a][b]
			end
		end
	end
	return -1
end

function hasAdj(x,y,id)
	return ((cols[x+1][y] == id) or (cols[x][y+1] == id) or (cols[x-1][y] == id) or (cols[x][y-1] == id))
end

function hasAdjNot(x,y,nId)
	return (((cols[x+1][y] >= 0) and (cols[x+1][y] ~= nId)) or ((cols[x][y+1] >= 0) and (cols[x][y+1] ~= nId)) or ((cols[x-1][y] >= 0) and (cols[x-1][y] ~= nId)) or ((cols[x][y-1] >= 0) and (cols[x][y-1] ~= nId)))
end

function isConnector(x,y,id)
	return (hasAdj(x,y,id) and hasAdjNot(x,y,id))
end

function floodFill(x, y)
	if ((x>=0) and (y>=0) and (x <= xLen) and (y <= yLen) and (cols[x][y] > 0)) then
		cols[x][y] = 0
		floodFill(x+1, y, id)
		floodFill(x-1, y, id)
		floodFill(x, y+1, id)
		floodFill(x, y-1, id)
	end
end

function merge(id)
	local conx = {}
	local cony = {}
	local cond = {}
	local conNum = 0
	for a=0,xLen,1 do
		for b=0,yLen,1 do
			if (isConnector(a,b,id)) then
				conx[conNum] = a
				cony[conNum] = b
				cond[conNum] = false;
				conNum = conNum + 1
			end
		end
	end
	if (conNum > 0) then
		for i=0,math.min(conNum-1,math.random(0, math.max(2,(conNum*0.05) + 2))),1 do
			local n = math.random(0,conNum-1)
			while (cond[n]) do
				n = math.random(0,conNum-1)
			end
			cols[conx[n]][cony[n]] = id
			cond[n] = true
		end
		floodFill(conx[0], cony[0])
	end
end

local nextID = getID()
while (nextID > 0) do
	print ("merging " .. nextID)
	merge(nextID)
	nextID = getID()
end

for a=0,xLen,1 do
	for b=0,yLen,1 do
		if (cols[a][b] < 0) then
			cols[a][b] = 0;
		else
			cols[a][b] = 1;
		end
	end
end
-- and everything after this is boilerplate

local retS = "["
for a=0,xLen,1 do
	retS = retS .. "["
	for b=0,yLen,1 do
		retS = retS .. cols[a][b]
		if b ~= yLen then
			retS = retS .. ","
		end
	end
	retS = retS .. "]"
	if a ~= xLen then
		retS = retS .. ","
	end
end
retS = retS .. "]"

return retS