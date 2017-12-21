$nums = 'list disk' | diskpart | %{if ($_ -match '\W(\d+)\W+脱机\W') {$Matches[1]}}
foreach($n in $nums)
{
    $se = 'select disk ' + $n
    $se, 'online disk' | diskpart
}