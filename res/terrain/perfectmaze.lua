local arg = table.pack(...)
local cols={}
local xLen = (arg[1]-1) or 1
local yLen = (arg[2]-1) or 1

-- Heres where the generation happens (above is boilerplate)
for a=0,xLen,1 do
	cols[a] = {}
	for b=0,yLen,1 do
		cols[a][b] = -1;
	end
end

function canBranch(x, y, dir)
	if (dir == 1) then
		return ((cols[x][y+1] == -1) and (cols[x][y+2] == -1))
	elseif (dir == 2) then
		return ((cols[x+1][y] == -1) and (cols[x+2][y] == -1))
	elseif (dir == 3) then
		return ((cols[x][y-1] == -1) and (cols[x][y-2] == -1))
	elseif (dir == 4) then
		return ((cols[x-1][y] == -1) and (cols[x-2][y] == -1))
	else
		print "Lua Error: perfectmaze.lua->canBranch()"
	end
end

function branch(x, y)
	while(canBranch(1) || canBranch(2) || canBranch(3) || canBranch(4)) do
		local dir = math.random(1,4)
		if (canBranch(dir)) then
			if (dir == 1) then
				cols[x][y+1] = 1
				cols[x][y+2] = 1
				branch(x, y+2)
			elseif (dir == 2) then
				cols[x+1][y] = 1
				cols[x+2][y] = 1
				branch(x+2, y)
			elseif (dir == 3) then
				cols[x][y-1] = 1
				cols[x][y-2] = 1
				branch(x, y-2)
			elseif (dir == 4) then
				cols[x-1][y] = 1
				cols[x-2][y] = 1
				branch(x-2, y)
			else
				print "Lua Error: perfectmaze.lua->branch()"
			end
		end
	end
end

branch((math.random((xLen-1)/2)*2)+1, (math.random((yLen-1)/2)*2)+1)

for a=0,xLen,1 do
	cols[a] = {}
	for b=0,yLen,1 do
		if (cols[a][b] == -1) then
			cols[a][b] = 0;
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